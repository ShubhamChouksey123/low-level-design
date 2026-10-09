package com.shubham.app.ratelimiter.ideal.service;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithmFactory;

import java.time.Duration;

public class FixedWindowRateLimitingAlgorithmFactory implements RateLimitingAlgorithmFactory {

    private final int maxRequestsPerWindow;
    private final Duration windowSize;

    public FixedWindowRateLimitingAlgorithmFactory(int maxRequestsPerWindow, Duration windowSize) {
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSize = windowSize;
    }

    @Override
    public RateLimitingAlgorithm create() {
        return new FixedWindowRateLimitingAlgorithm(maxRequestsPerWindow, windowSize);
    }
}
