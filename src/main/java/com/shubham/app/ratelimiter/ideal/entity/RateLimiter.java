package com.shubham.app.ratelimiter.ideal.entity;

import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RateLimiter {

    private final RateLimitingAlgorithmFactory algorithmFactory;
    private final Map<String, RateLimitingAlgorithm> clientIdToAlgorithm = new HashMap<>();

    public RateLimiter(RateLimitingAlgorithmFactory algorithmFactory) {
        if (algorithmFactory == null) {
            throw new InvalidRateLimiterConfigException("algorithmFactory must not be null");
        }
        this.algorithmFactory = algorithmFactory;
    }

    public synchronized boolean allowRequest(String clientId, Instant timestamp) {
        RateLimitingAlgorithm algorithm = clientIdToAlgorithm.get(clientId);
        if (algorithm == null) {
            algorithm = algorithmFactory.create();
            clientIdToAlgorithm.put(clientId, algorithm);
        }
        return algorithm.allowRequest(timestamp);
    }
}
