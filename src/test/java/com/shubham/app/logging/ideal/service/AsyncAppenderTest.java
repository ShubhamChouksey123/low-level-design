package com.shubham.app.logging.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.LogEvent;
import com.shubham.app.logging.ideal.entity.LogLevel;
import com.shubham.app.logging.ideal.exception.InvalidLoggingConfigException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncAppenderTest {

    private static final Duration NEVER = Duration.ofHours(1);

    /**
     * Thread-safe stand-in destination that can optionally block or fail on demand.
     */
    private static class RecordingAppender implements Appender {
        final List<String> messages = Collections.synchronizedList(new ArrayList<>());
        final AtomicInteger flushCount = new AtomicInteger();
        volatile boolean closed;
        volatile String failOn;
        volatile CountDownLatch blockFirstAppendUntil;
        final CountDownLatch firstAppendStarted = new CountDownLatch(1);

        @Override
        public void append(LogEvent event) {
            firstAppendStarted.countDown();
            if (blockFirstAppendUntil != null) {
                try {
                    blockFirstAppendUntil.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (event.message().equals(failOn)) {
                throw new IllegalStateException("boom");
            }
            messages.add(event.message());
        }

        @Override
        public void flush() {
            flushCount.incrementAndGet();
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static LogEvent event(String message) {
        return new LogEvent(Instant.EPOCH, LogLevel.INFO, "logger", "main", null, message);
    }

    private static void awaitTrue(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!condition.getAsBoolean()) {
            if (System.nanoTime() > deadline) {
                throw new AssertionError("condition not met within 5s");
            }
            Thread.sleep(5);
        }
    }

    @Test
    void closeDeliversEveryQueuedEventInOrderThenClosesTheDelegate() {
        RecordingAppender delegate = new RecordingAppender();
        AsyncAppender async = new AsyncAppender(delegate, 100, 1000, NEVER, OverflowPolicy.BLOCK);

        for (int i = 0; i < 50; i++) {
            async.append(event("m" + i));
        }
        async.close();

        assertEquals(50, delegate.messages.size());
        assertEquals("m0", delegate.messages.get(0));
        assertEquals("m49", delegate.messages.get(49));
        assertTrue(delegate.closed);
        assertTrue(delegate.flushCount.get() >= 1);
    }

    @Test
    void flushesAsSoonAsTheBatchIsFullEvenWithALongInterval() throws InterruptedException {
        RecordingAppender delegate = new RecordingAppender();
        AsyncAppender async = new AsyncAppender(delegate, 100, 3, NEVER, OverflowPolicy.BLOCK);

        async.append(event("a"));
        async.append(event("b"));
        async.append(event("c"));

        awaitTrue(() -> delegate.flushCount.get() >= 1);
        assertEquals(3, delegate.messages.size());
        async.close();
    }

    @Test
    void flushesAfterTheIntervalEvenWhenTheBatchIsNotFull() throws InterruptedException {
        RecordingAppender delegate = new RecordingAppender();
        AsyncAppender async = new AsyncAppender(delegate, 100, 1000, Duration.ofMillis(50), OverflowPolicy.BLOCK);

        async.append(event("lonely"));

        awaitTrue(() -> delegate.flushCount.get() >= 1);
        assertEquals(1, delegate.messages.size());
        async.close();
    }

    @Test
    void dropNewestCountsEventsThatDoNotFitInsteadOfBlocking() throws InterruptedException {
        RecordingAppender delegate = new RecordingAppender();
        CountDownLatch release = new CountDownLatch(1);
        delegate.blockFirstAppendUntil = release;
        AsyncAppender async = new AsyncAppender(delegate, 2, 1000, NEVER, OverflowPolicy.DROP_NEWEST);

        async.append(event("held by the slow destination"));
        // Wait until the worker has taken it, so the queue (capacity 2) is empty and
        // can be filled
        // exactly.
        assertTrue(delegate.firstAppendStarted.await(5, TimeUnit.SECONDS));
        async.append(event("q1"));
        async.append(event("q2"));
        async.append(event("dropped-1"));
        async.append(event("dropped-2"));

        assertEquals(2, async.getDroppedCount());
        release.countDown();
        async.close();
        assertEquals(3, delegate.messages.size());
    }

    @Test
    void eventsAppendedAfterCloseAreCountedAsDropped() {
        RecordingAppender delegate = new RecordingAppender();
        AsyncAppender async = new AsyncAppender(delegate, 10, 10, NEVER, OverflowPolicy.BLOCK);
        async.close();

        async.append(event("too late"));

        assertEquals(1, async.getDroppedCount());
        assertEquals(0, delegate.messages.size());
    }

    @Test
    void aDelegateFailureOnOneEventDoesNotKillTheWorker() {
        RecordingAppender delegate = new RecordingAppender();
        delegate.failOn = "bad";
        AsyncAppender async = new AsyncAppender(delegate, 10, 10, NEVER, OverflowPolicy.BLOCK);

        async.append(event("good-1"));
        async.append(event("bad"));
        async.append(event("good-2"));
        async.close();

        assertEquals(List.of("good-1", "good-2"), delegate.messages);
    }

    @Test
    void concurrentProducersLoseNothingAndKeepEachThreadsOwnOrder() throws InterruptedException {
        int threads = 8;
        int perThread = 500;
        RecordingAppender delegate = new RecordingAppender();
        // Capacity far below the total, so producers really do block on a full queue.
        AsyncAppender async = new AsyncAppender(delegate, 16, 50, NEVER, OverflowPolicy.BLOCK);
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t++) {
            int thread = t;
            pool.submit(() -> {
                for (int i = 0; i < perThread; i++) {
                    async.append(event(thread + ":" + i));
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        async.close();

        assertEquals(threads * perThread, delegate.messages.size());
        assertEquals(0, async.getDroppedCount());
        for (int t = 0; t < threads; t++) {
            String prefix = t + ":";
            List<Integer> seen = delegate.messages.stream().filter(m -> m.startsWith(prefix))
                    .map(m -> Integer.parseInt(m.substring(prefix.length()))).toList();
            assertEquals(perThread, seen.size());
            for (int i = 0; i < perThread; i++) {
                assertEquals(i, seen.get(i));
            }
        }
    }

    @Test
    void rejectsInvalidConfiguration() {
        RecordingAppender delegate = new RecordingAppender();
        assertThrows(InvalidLoggingConfigException.class,
                () -> new AsyncAppender(null, 10, 10, NEVER, OverflowPolicy.BLOCK));
        assertThrows(InvalidLoggingConfigException.class,
                () -> new AsyncAppender(delegate, 0, 10, NEVER, OverflowPolicy.BLOCK));
        assertThrows(InvalidLoggingConfigException.class,
                () -> new AsyncAppender(delegate, 10, 0, NEVER, OverflowPolicy.BLOCK));
        assertThrows(InvalidLoggingConfigException.class,
                () -> new AsyncAppender(delegate, 10, 10, Duration.ZERO, OverflowPolicy.BLOCK));
        assertThrows(InvalidLoggingConfigException.class, () -> new AsyncAppender(delegate, 10, 10, NEVER, null));
    }
}
