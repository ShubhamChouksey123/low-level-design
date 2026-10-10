package com.shubham.app.ratelimiter.ideal.service;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

/**
 * Token bucket: starts full with {@code bucketCapacity} tokens, refills
 * continuously at {@code
 * tokensPerSecond} (capped at {@code bucketCapacity}), and a request consumes
 * one token - rejected only when less than a full token is available. Refill is
 * computed lazily from the elapsed time between consecutive calls, rather than
 * on a background timer. The baseline is seeded from the *first call's own
 * timestamp*, not the wall clock, so it stays consistent with callers that
 * drive this with historical/fixed timestamps.
 *
 * <p>
 * Token counts are tracked as {@link BigDecimal}, not {@code double} - elapsed
 * nanoseconds divided by a power of ten (1e9) is always an exact, terminating
 * decimal, so comparisons against a whole token (e.g. "is there at least 1
 * available?") are exact too, with no epsilon tolerance needed for
 * floating-point rounding.
 */
public class TokenBucketRateLimitingAlgorithm implements RateLimitingAlgorithm {

    private static final BigDecimal NANOS_PER_SECOND = BigDecimal.valueOf(1_000_000_000L);
    private static final int ELAPSED_SECONDS_SCALE = 9;

    private final BigDecimal bucketCapacity;
    private final BigDecimal tokensPerSecond;

    private BigDecimal availableTokens;
    private Instant lastRefillTimestamp;

    public TokenBucketRateLimitingAlgorithm(int bucketCapacity, double tokensPerSecond) {
        if (bucketCapacity <= 0) {
            throw new InvalidRateLimiterConfigException("bucketCapacity must be positive: " + bucketCapacity);
        }
        if (tokensPerSecond <= 0) {
            throw new InvalidRateLimiterConfigException("tokensPerSecond must be positive: " + tokensPerSecond);
        }
        this.bucketCapacity = BigDecimal.valueOf(bucketCapacity);
        this.tokensPerSecond = BigDecimal.valueOf(tokensPerSecond);
        this.availableTokens = this.bucketCapacity;
    }

    @Override
    public synchronized boolean allowRequest(Instant timestamp) {
        if (lastRefillTimestamp != null) {
            BigDecimal elapsedNanos = BigDecimal.valueOf(Duration.between(lastRefillTimestamp, timestamp).toNanos());
            BigDecimal elapsedSeconds = elapsedNanos.divide(NANOS_PER_SECOND, ELAPSED_SECONDS_SCALE,
                    RoundingMode.HALF_UP);
            BigDecimal refilledTokens = elapsedSeconds.multiply(tokensPerSecond);
            availableTokens = availableTokens.add(refilledTokens).min(bucketCapacity);
        }
        lastRefillTimestamp = timestamp;

        if (availableTokens.compareTo(BigDecimal.ONE) < 0) {
            return false;
        }

        availableTokens = availableTokens.subtract(BigDecimal.ONE);
        return true;
    }
}
