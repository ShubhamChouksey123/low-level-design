package com.shubham.app.ratelimiter.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketRateLimitingAlgorithmTest {

    @Test
    void constructorRejectsANonPositiveBucketCapacity() {
        assertThrows(InvalidRateLimiterConfigException.class, () -> new TokenBucketRateLimitingAlgorithm(0, 1.0));
        assertThrows(InvalidRateLimiterConfigException.class, () -> new TokenBucketRateLimitingAlgorithm(-1, 1.0));
    }

    @Test
    void constructorRejectsANonPositiveRefillRate() {
        assertThrows(InvalidRateLimiterConfigException.class, () -> new TokenBucketRateLimitingAlgorithm(3, 0.0));
        assertThrows(InvalidRateLimiterConfigException.class, () -> new TokenBucketRateLimitingAlgorithm(3, -1.0));
    }

    @Test
    void theBucketStartsFullSoABurstUpToCapacityIsAllowedImmediately() {
        RateLimitingAlgorithm algorithm = new TokenBucketRateLimitingAlgorithm(3, 1.0);
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(now));
        assertTrue(algorithm.allowRequest(now));
        assertTrue(algorithm.allowRequest(now));
        assertFalse(algorithm.allowRequest(now));
    }

    // The exact bucketCapacity=3/rate=1-per-second scenario from the practice/ code
    // review, whose
    // Math.max/Math.min inversion let all 6 of these requests through. This fixed
    // version matches
    // the correct hand-traced math: 3 initial tokens cover the first 3 calls
    // (10/20/30ms apart),
    // the 4th and 5th are rejected since well under a second has passed, and the
    // 6th (930ms after
    // the 5th) is allowed again once ~0.93 tokens refill on top of the ~0.07 left
    // over.
    @Test
    void refillsFractionallyOverTimeInsteadOfResettingOrTruncatingToZero() {
        RateLimitingAlgorithm algorithm = new TokenBucketRateLimitingAlgorithm(3, 1.0);
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base.plusMillis(10)));
        assertTrue(algorithm.allowRequest(base.plusMillis(20)));
        assertTrue(algorithm.allowRequest(base.plusMillis(30)));
        assertFalse(algorithm.allowRequest(base.plusMillis(40)));
        assertFalse(algorithm.allowRequest(base.plusMillis(80)));
        assertTrue(algorithm.allowRequest(base.plusMillis(1010)));
    }

    @Test
    void refillNeverExceedsBucketCapacityEvenAfterALongIdlePeriod() {
        RateLimitingAlgorithm algorithm = new TokenBucketRateLimitingAlgorithm(3, 1.0);
        Instant base = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(algorithm.allowRequest(base));

        Instant muchLater = base.plusSeconds(1000);
        // If refill weren't capped at bucketCapacity, ~999 tokens would have accrued by
        // now,
        // letting far more than bucketCapacity requests through in this burst.
        assertTrue(algorithm.allowRequest(muchLater));
        assertTrue(algorithm.allowRequest(muchLater));
        assertTrue(algorithm.allowRequest(muchLater));
        assertFalse(algorithm.allowRequest(muchLater));
    }
}
