package com.shubham.app.ratelimiter.practice.entity;

import java.time.Instant;

public interface RateLimitAlgorithm {

    boolean addRequest(Instant timestamp);
}
