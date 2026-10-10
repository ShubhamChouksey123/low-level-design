package com.shubham.app.ratelimiter.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;
import com.shubham.app.ratelimiter.ideal.service.FixedWindowRateLimitingAlgorithmFactory;
import com.shubham.app.ratelimiter.ideal.service.SlidingWindowRateLimitingAlgorithmFactory;
import com.shubham.app.ratelimiter.ideal.service.TokenBucketRateLimitingAlgorithmFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    void constructorRejectsANullAlgorithmFactory() {
        assertThrows(InvalidRateLimiterConfigException.class, () -> new RateLimiter(null));
    }

    @Test
    void eachClientGetsItsOwnIndependentWindow() {
        RateLimiter rateLimiter = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(1, Duration.ofSeconds(10)));
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(rateLimiter.allowRequest("user1", now));
        assertFalse(rateLimiter.allowRequest("user1", now));
        // A different client has never been seen before, so it gets a fresh window -
        // the limit
        // is per-client, not global.
        assertTrue(rateLimiter.allowRequest("user2", now));
    }

    @Test
    void swappingTheAlgorithmFactoryChangesTheEnforcedAlgorithmWithoutTouchingRateLimiter() {
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        RateLimiter fixedWindow = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(1, Duration.ofSeconds(10)));
        RateLimiter slidingWindow = new RateLimiter(
                new SlidingWindowRateLimitingAlgorithmFactory(1, Duration.ofSeconds(10)));

        assertTrue(fixedWindow.allowRequest("user1", base));
        assertTrue(slidingWindow.allowRequest("user1", base));

        // Fixed window: a request inside the same 10s window is rejected even though
        // the first
        // request's window hasn't reset yet.
        assertFalse(fixedWindow.allowRequest("user1", base.plusSeconds(9)));
        // Sliding window: same timestamps, same limit - still rejected, since t=0
        // hasn't aged
        // out of the last 10s as of t=9.
        assertFalse(slidingWindow.allowRequest("user1", base.plusSeconds(9)));
    }

    // The classic fixed-window edge-burst problem from rate-limiter.md: a client
    // sends N
    // requests near the end of one window and another N right after it resets, so a
    // short real
    // time span ends up carrying far more than N requests. One request opens a 10s
    // window at
    // t=0; two more land late in that window (t=9.8, t=9.9); then three more land
    // just past the
    // 10s boundary (t=10.0, t=10.05, t=10.1), triggering a fixed-window reset.
    //
    // Fixed window WRONGLY allows all 6: the reset at t=10.0 wipes the whole
    // window's count back
    // to zero, discarding the fact that t=9.8/t=9.9 are still only fractions of a
    // second old - so
    // 5 of the 6 requests (t=9.8 through t=10.1) land within a trailing 10s span,
    // double the
    // limit of 3.
    //
    // Sliding window CORRECTLY allows only 4 (t=0, t=9.8, t=9.9, t=10.0) and
    // rejects the last two
    // (t=10.05, t=10.1): it ages entries out one at a time instead of resetting
    // everything at
    // once, so at every instant it accurately counts "how many requests in the last
    // 10s" and
    // never lets more than 3 through in any trailing 10s window.
    @Test
    void fixedWindowWronglyAllowsAnEdgeBurstThatSlidingWindowCorrectlyRejects() {
        int maxRequestsPerWindow = 3;
        Duration windowSize = Duration.ofSeconds(10);
        RateLimiter fixedWindow = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(maxRequestsPerWindow, windowSize));
        RateLimiter slidingWindow = new RateLimiter(
                new SlidingWindowRateLimitingAlgorithmFactory(maxRequestsPerWindow, windowSize));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        long[] offsetsMillis = {0, 9_800, 9_900, 10_000, 10_050, 10_100};

        boolean[] fixedResults = new boolean[offsetsMillis.length];
        boolean[] slidingResults = new boolean[offsetsMillis.length];
        for (int i = 0; i < offsetsMillis.length; i++) {
            Instant timestamp = base.plusMillis(offsetsMillis[i]);
            fixedResults[i] = fixedWindow.allowRequest("user1", timestamp);
            slidingResults[i] = slidingWindow.allowRequest("user1", timestamp);
        }

        assertArrayEquals(new boolean[]{true, true, true, true, true, true}, fixedResults, "fixed window");
        assertArrayEquals(new boolean[]{true, true, true, true, false, false}, slidingResults, "sliding window");
    }

    // Proves the ④ extensibility claim in ideal/README.md: Token Bucket plugs into
    // RateLimiter
    // through the same RateLimitingAlgorithmFactory seam as Fixed/Sliding Window,
    // with zero
    // changes to RateLimiter itself.
    @Test
    void tokenBucketPlugsIntoRateLimiterThroughTheSameFactorySeam() {
        RateLimiter rateLimiter = new RateLimiter(new TokenBucketRateLimitingAlgorithmFactory(2, 1.0));
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(rateLimiter.allowRequest("user1", now));
        assertTrue(rateLimiter.allowRequest("user1", now));
        assertFalse(rateLimiter.allowRequest("user1", now));
    }

    @Test
    void concurrentFirstRequestsForTheSameNewClientNeverLoseAWindow() throws InterruptedException {
        int maxRequestsPerWindow = 5;
        RateLimiter rateLimiter = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(maxRequestsPerWindow, Duration.ofSeconds(10)));
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger allowedCount = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (rateLimiter.allowRequest("user1", now)) {
                        allowedCount.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // If RateLimiter's computeIfAbsent lost the create-once-per-key race, or if the
        // algorithm's own allowRequest weren't synchronized, two threads could each see
        // a
        // consistent-looking state and both be let through, allowing more than
        // maxRequestsPerWindow.
        assertEquals(maxRequestsPerWindow, allowedCount.get());
    }

    // RateLimiter intentionally does not hold one lock around every client's call -
    // only
    // concurrent requests for the *same* client need to be serialized (by that
    // client's own
    // algorithm instance), not requests for different clients. This fires many
    // different
    // clients concurrently, each hammering well past its own limit, and checks
    // every single
    // client's count independently - a bug in the per-key get-or-create logic (e.g.
    // two threads
    // building separate algorithm instances for the same new client) would show up
    // as some
    // client's count being wrong, not just the aggregate.
    @Test
    void differentClientsAreRateLimitedIndependentlyUnderConcurrentAccess() throws InterruptedException {
        int maxRequestsPerWindow = 3;
        int clientCount = 20;
        int requestsPerClient = 10;
        RateLimiter rateLimiter = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(maxRequestsPerWindow, Duration.ofSeconds(10)));
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        ConcurrentMap<String, AtomicInteger> allowedCountByClient = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        for (int c = 0; c < clientCount; c++) {
            String clientId = "user" + c;
            allowedCountByClient.put(clientId, new AtomicInteger());
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestsPerClient; i++) {
                        if (rateLimiter.allowRequest(clientId, now)) {
                            allowedCountByClient.get(clientId).incrementAndGet();
                        }
                    }
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        for (AtomicInteger allowedCount : allowedCountByClient.values()) {
            assertEquals(maxRequestsPerWindow, allowedCount.get());
        }
    }
}
