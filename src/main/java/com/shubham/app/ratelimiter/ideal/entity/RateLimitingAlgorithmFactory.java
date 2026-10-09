package com.shubham.app.ratelimiter.ideal.entity;

/**
 * The Strategy: builds a fresh, independent {@link RateLimitingAlgorithm}
 * instance for a client on its first request. Which algorithm a
 * {@link RateLimiter} enforces is decided entirely by which factory it was
 * constructed with - swapping algorithms never touches {@link RateLimiter}.
 */
public interface RateLimitingAlgorithmFactory {

    RateLimitingAlgorithm create();
}
