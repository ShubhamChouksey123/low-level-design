package com.shubham.app.ratelimiter.ideal.entity;

import java.time.Instant;

/**
 * One client's worth of rate-limiting state and decision logic for a single
 * algorithm (fixed window, sliding window, ...). A fresh instance is created
 * per client by a {@link RateLimitingAlgorithmFactory} - instances are never
 * shared across clients.
 *
 * <p>
 * Implementations must make {@link #allowRequest} a {@code synchronized}
 * method. {@link RateLimiter} deliberately does not hold one lock around every
 * client's call - different clients must be able to proceed in parallel - so
 * each algorithm instance is responsible for serializing concurrent calls
 * against its own state. Java doesn't allow {@code synchronized} on an
 * interface method signature, so this is a documented contract rather than a
 * compiler-enforced one; see any of the concrete implementations for the
 * expected shape.
 */
public interface RateLimitingAlgorithm {

    boolean allowRequest(Instant timestamp);
}
