package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;
import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithmFactory;

public class TokenBucketRateLimitAlgorithmFactory implements RateLimitAlgorithmFactory {

    private final int bucketSize;
    private final double rate;

    public TokenBucketRateLimitAlgorithmFactory(int bucketSize, double rate) {
        this.bucketSize = bucketSize;
        this.rate = rate;
    }

    @Override
    public RateLimitAlgorithm create() {
        return new TokenBucketRateLimitAlgorithm(bucketSize, rate);
    }
}
