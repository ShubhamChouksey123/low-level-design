package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

public class TokenBucketRateLimitAlgorithm implements RateLimitAlgorithm {

    private static final BigDecimal NANOS_PER_SECONDS = BigDecimal.valueOf(1_000_000_000);
    private static final int ELAPSED_SECONDS_SCALE = 9;

    private final BigDecimal bucketSize;
    private final BigDecimal tokensPerSecond;

    private BigDecimal availableTokens;
    private Instant lastRefillTimestamp;

    public TokenBucketRateLimitAlgorithm(int bucketSize, double tokensPerSecond) {
        this.bucketSize = BigDecimal.valueOf(bucketSize);
        this.tokensPerSecond = BigDecimal.valueOf(tokensPerSecond);
        this.lastRefillTimestamp = Instant.now();
        this.availableTokens = BigDecimal.valueOf(bucketSize);
    }

    @Override
    public synchronized boolean addRequest(Instant timestamp) {

        if (lastRefillTimestamp != null) {

            BigDecimal elapsedNanos = BigDecimal.valueOf(Duration.between(lastRefillTimestamp, timestamp).toNanos());
            BigDecimal elapsedSecond = elapsedNanos.divide(NANOS_PER_SECONDS, ELAPSED_SECONDS_SCALE,
                    RoundingMode.HALF_UP);
            BigDecimal refilledTokens = elapsedSecond.multiply(tokensPerSecond);
            availableTokens = availableTokens.add(refilledTokens).min(bucketSize);
        }

        lastRefillTimestamp = timestamp;
        if (availableTokens.compareTo(BigDecimal.ONE) < 0) {
            return false;
        }
        availableTokens = availableTokens.subtract(BigDecimal.ONE);
        return true;
    }
}
