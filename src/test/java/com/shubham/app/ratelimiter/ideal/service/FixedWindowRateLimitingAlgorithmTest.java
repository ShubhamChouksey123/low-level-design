package com.shubham.app.ratelimiter.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedWindowRateLimitingAlgorithmTest {

    @Test
    void constructorRejectsANonPositiveLimit() {
        assertThrows(InvalidRateLimiterConfigException.class,
                () -> new FixedWindowRateLimitingAlgorithm(0, Duration.ofSeconds(1)));
    }

    @Test
    void constructorRejectsANonPositiveWindowSize() {
        assertThrows(InvalidRateLimiterConfigException.class,
                () -> new FixedWindowRateLimitingAlgorithm(3, Duration.ZERO));
        assertThrows(InvalidRateLimiterConfigException.class, () -> new FixedWindowRateLimitingAlgorithm(3, null));
    }

    // The exact worked example from rate-limiter.md: N=3, 10s window, requests at
    // t=0,2,5,7,11s. t=7 is rejected (would be the 4th in-window request), t=11 is
    // a fresh
    // window so it's allowed again - a burst can straddle a window boundary.
    @Test
    void followsTheRateLimiterMdWorkedExample() {
        RateLimitingAlgorithm algorithm = new FixedWindowRateLimitingAlgorithm(3, Duration.ofSeconds(10));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base.plusSeconds(0)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(2)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(5)));
        assertFalse(algorithm.allowRequest(base.plusSeconds(7)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(11)));
    }

    @Test
    void aRequestExactlyAtTheWindowBoundaryStartsAFreshWindow() {
        RateLimitingAlgorithm algorithm = new FixedWindowRateLimitingAlgorithm(1, Duration.ofSeconds(10));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base));
        assertFalse(algorithm.allowRequest(base.plusSeconds(5)));
        assertTrue(algorithm.allowRequest(base.plusSeconds(10)));
    }

    @Test
    void allowedRequestsWithinTheSameWindowShareOneCount() {
        RateLimitingAlgorithm algorithm = new FixedWindowRateLimitingAlgorithm(2, Duration.ofSeconds(10));
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base));
        assertTrue(algorithm.allowRequest(base.plusSeconds(1)));
        assertFalse(algorithm.allowRequest(base.plusSeconds(2)));
    }
}
