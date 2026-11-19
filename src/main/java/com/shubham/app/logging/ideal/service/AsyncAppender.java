package com.shubham.app.logging.ideal.service;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.LogEvent;
import com.shubham.app.logging.ideal.entity.LogLevel;
import com.shubham.app.logging.ideal.exception.InvalidLoggingConfigException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Decorator that makes any appender asynchronous: callers only enqueue, and one
 * worker thread hands events to the delegate in arrival order. The worker asks
 * the delegate to {@code flush()} when {@code batchSize} events have been
 * delivered or {@code flushInterval} has passed, whichever comes first.
 */
public class AsyncAppender implements Appender {

    private static final LogEvent POISON = new LogEvent(Instant.EPOCH, LogLevel.TRACE, "", "", null, "");

    private final Appender delegate;
    private final BlockingQueue<LogEvent> queue;
    private final int batchSize;
    private final long flushIntervalNanos;
    private final OverflowPolicy overflowPolicy;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicLong droppedCount = new AtomicLong();
    private final Thread worker;

    public AsyncAppender(Appender delegate, int queueCapacity, int batchSize, Duration flushInterval,
            OverflowPolicy overflowPolicy) {
        if (delegate == null || overflowPolicy == null) {
            throw new InvalidLoggingConfigException("delegate and overflowPolicy are required");
        }
        if (queueCapacity <= 0 || batchSize <= 0) {
            throw new InvalidLoggingConfigException("queueCapacity and batchSize must be positive");
        }
        if (flushInterval == null || flushInterval.isZero() || flushInterval.isNegative()) {
            throw new InvalidLoggingConfigException("flushInterval must be positive");
        }
        this.delegate = delegate;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.batchSize = batchSize;
        this.flushIntervalNanos = flushInterval.toNanos();
        this.overflowPolicy = overflowPolicy;
        this.worker = new Thread(this::runWorker, "async-appender-worker");
        this.worker.setDaemon(true);
        this.worker.start();
    }

    @Override
    public void append(LogEvent event) {
        if (closed.get()) {
            droppedCount.incrementAndGet();
            return;
        }
        if (overflowPolicy == OverflowPolicy.DROP_NEWEST) {
            if (!queue.offer(event)) {
                droppedCount.incrementAndGet();
            }
            return;
        }
        try {
            // Re-check `closed` while waiting so a full queue never strands a caller after
            // close().
            while (!queue.offer(event, 50, TimeUnit.MILLISECONDS)) {
                if (closed.get()) {
                    droppedCount.incrementAndGet();
                    return;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            droppedCount.incrementAndGet();
        }
    }

    /**
     * Stops accepting events, delivers everything already queued, flushes and
     * closes the delegate.
     */
    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            // Queued behind every earlier event, so the worker sees it only after draining
            // them.
            queue.put(POISON);
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public long getDroppedCount() {
        return droppedCount.get();
    }

    private void runWorker() {
        int sinceFlush = 0;
        long lastFlush = System.nanoTime();
        try {
            while (true) {
                long untilFlush = flushIntervalNanos - (System.nanoTime() - lastFlush);
                LogEvent event = queue.poll(Math.max(untilFlush, 0), TimeUnit.NANOSECONDS);
                if (event == POISON) {
                    break;
                }
                if (event != null) {
                    deliver(event);
                    sinceFlush++;
                }
                if (sinceFlush >= batchSize || System.nanoTime() - lastFlush >= flushIntervalNanos) {
                    if (sinceFlush > 0) {
                        flushDelegate();
                    }
                    sinceFlush = 0;
                    lastFlush = System.nanoTime();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            flushDelegate();
            try {
                delegate.close();
            } catch (RuntimeException e) {
                System.err.println("Closing delegate failed: " + e);
            }
        }
    }

    private void deliver(LogEvent event) {
        try {
            delegate.append(event);
        } catch (RuntimeException e) {
            // A bad event or a failing destination must not kill the only worker thread.
            System.err.println("Delegate append failed: " + e);
        }
    }

    private void flushDelegate() {
        try {
            delegate.flush();
        } catch (RuntimeException e) {
            System.err.println("Delegate flush failed: " + e);
        }
    }
}
