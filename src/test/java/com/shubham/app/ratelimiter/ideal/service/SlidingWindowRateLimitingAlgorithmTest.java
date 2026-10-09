package com.shubham.app.ratelimiter.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingWindowRateLimitingAlgorithmTest {

    @Test
    void constructorRejectsANonPositiveLimit() {
        assertThrows(InvalidRateLimiterConfigException.class,
                () -> new SlidingWindowRateLimitingAlgorithm(0, Duration.ofSeconds(1)));
    }

    @Test
    void constructorRejectsANonPositiveWindowSize() {
        assertThrows(InvalidRateLimiterConfigException.class,
                () -> new SlidingWindowRateLimitingAlgorithm(3, Duration.ZERO));
        assertThrows(InvalidRateLimiterConfigException.class, () -> new SlidingWindowRateLimitingAlgorithm(3, null));
    }

    // Same N=3/10s window and timestamps as rate-limiter.md's worked example,
    // through t=7.
    // t=11 diverges from that doc's informal write-up on purpose: this algorithm's
    // log only
    // ever holds *allowed* requests (a rejected request never happened, so it can't
    // consume
    // quota) - rate-limiter.md's doc counts the rejected t=7 request as still
    // occupying a
    // window slot, which would need a 4th in-window entry at t=11 under a limit of
    // 3; that
    // reading isn't internally consistent with "N=3" either (its own explanation
    // says "2
    // earlier requests" remain, which alone wouldn't exceed the limit). This test
    // documents
    // the behavior this implementation actually has: only t=2 and t=5 are still
    // within 10s of
    // t=11 once t=0 ages out, so the window has room and t=11 is allowed.
    @Test
    void onlyAcceptedRequestsConsumeTheSlidingWindowsQuota() {
        RateLimitingAlgorithm algorithm = new SlidingWindowRateLimitingAlgorithm(3, Duration.ofSeconds(10));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base.plusSeconds(0)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(2)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(5)));
        assertFalse(algorithm.allowRequest(base.plusSeconds(7)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(11)));
    }

    // The actual point of a sliding window: unlike a fixed window, there's no reset
    // boundary a
    // burst can exploit. A fixed window would reset entirely once elapsed >=
    // windowSize from the
    // window's first entry; a sliding window never resets wholesale - it only ages
    // out entries
    // one at a time, so a client that was throttled at the limit never earns a
    // sudden burst.
    @Test
    void theWindowNeverResetsWholesaleItOnlyAgesOutIndividualEntries() {
        RateLimitingAlgorithm algorithm = new SlidingWindowRateLimitingAlgorithm(2, Duration.ofSeconds(10));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base));
        assertTrue(algorithm.allowRequest(base.plusSeconds(5)));
        // Still within 10s of t=0, so the window is still full - this is the request a
        // fixed
        // window (anchored at t=0, 10s wide) would also still reject.
        assertFalse(algorithm.allowRequest(base.plusSeconds(9)));
        // t=0 has now aged out (10s elapsed), but t=5 has not - exactly one slot freed
        // up.
        assertTrue(algorithm.allowRequest(base.plusSeconds(10)));
        assertFalse(algorithm.allowRequest(base.plusSeconds(10)));
    }
}
