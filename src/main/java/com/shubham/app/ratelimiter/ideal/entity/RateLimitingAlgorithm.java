package com.shubham.app.ratelimiter.ideal.entity;

import java.time.Instant;

/**
 * One client's worth of rate-limiting state and decision logic for a single
 * algorithm (fixed window, sliding window, ...). A fresh instance is created
 * per client by a {@link RateLimitingAlgorithmFactory} - instances are never
 * shared across clients.
 */
public interface RateLimitingAlgorithm {

    boolean allowRequest(Instant timestamp);
}
