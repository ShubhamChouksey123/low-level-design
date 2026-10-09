package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;
import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithmFactory;

import java.time.Duration;

public class SlidingWindowCounterAlgorithmFactory implements RateLimitAlgorithmFactory {

    private final int maxRequestsPerWindow;
    private final Duration windowSize;

    public SlidingWindowCounterAlgorithmFactory(int maxRequestsPerWindow, Duration windowSize) {
        this.windowSize = windowSize;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
    }

    @Override
    public RateLimitAlgorithm create() {
        return new SlidingWindowCounterAlgorithm(maxRequestsPerWindow, windowSize);
    }
}
