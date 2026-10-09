package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;
import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithmFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RateLimiterService {

    private Map<Integer, RateLimitAlgorithm> userToChecker;
    private RateLimitAlgorithmFactory rateLimitAlgorithmFactory;

    public RateLimiterService(RateLimitAlgorithmFactory rateLimitAlgorithmFactory) {
        userToChecker = new HashMap<>();
        this.rateLimitAlgorithmFactory = rateLimitAlgorithmFactory;
    }

    public boolean checkRateLimitStatus(Integer userId, Instant instant) {

        RateLimitAlgorithm checker = userToChecker.get(userId);

        if (checker == null) {
            // first time call
            checker = rateLimitAlgorithmFactory.create();
            userToChecker.put(userId, checker);
        }
        return checker.addRequest(instant);
    }
}
