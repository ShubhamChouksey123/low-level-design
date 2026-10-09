package com.shubham.app.ratelimiter.ideal.service;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithmFactory;

public class TokenBucketRateLimitingAlgorithmFactory implements RateLimitingAlgorithmFactory {

    private final int bucketCapacity;
    private final double tokensPerSecond;

    public TokenBucketRateLimitingAlgorithmFactory(int bucketCapacity, double tokensPerSecond) {
        this.bucketCapacity = bucketCapacity;
        this.tokensPerSecond = tokensPerSecond;
    }

    @Override
    public RateLimitingAlgorithm create() {
        return new TokenBucketRateLimitingAlgorithm(bucketCapacity, tokensPerSecond);
    }
}
