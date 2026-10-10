package com.shubham.app.ratelimiter.ideal.entity;

import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class RateLimiter {

    private final RateLimitingAlgorithmFactory algorithmFactory;
    private final ConcurrentMap<String, RateLimitingAlgorithm> clientIdToAlgorithm = new ConcurrentHashMap<>();

    public RateLimiter(RateLimitingAlgorithmFactory algorithmFactory) {
        if (algorithmFactory == null) {
            throw new InvalidRateLimiterConfigException("algorithmFactory must not be null");
        }
        this.algorithmFactory = algorithmFactory;
    }

    public boolean allowRequest(String clientId, Instant timestamp) {
        RateLimitingAlgorithm algorithm = clientIdToAlgorithm.computeIfAbsent(clientId,
                new Function<String, RateLimitingAlgorithm>() {
                    @Override
                    public RateLimitingAlgorithm apply(String id) {
                        return algorithmFactory.create();
                    }
                });
        return algorithm.allowRequest(timestamp);
    }
}
