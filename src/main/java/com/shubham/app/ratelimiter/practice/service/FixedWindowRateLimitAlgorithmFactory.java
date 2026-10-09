package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;
import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithmFactory;

import java.time.Duration;

public class FixedWindowRateLimitAlgorithmFactory implements RateLimitAlgorithmFactory {

    private int maxLimit;
    private Duration rollingInterval;

    public FixedWindowRateLimitAlgorithmFactory(int maxLimit, Duration rollingInterval) {
        this.maxLimit = maxLimit;
        this.rollingInterval = rollingInterval;
    }

    @Override
    public RateLimitAlgorithm create() {
        return new FixWindowCounterAlgorithm(this.maxLimit, this.rollingInterval);
    }
}
