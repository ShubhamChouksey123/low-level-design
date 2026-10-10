package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class FixWindowCounterAlgorithm implements RateLimitAlgorithm {

    private Deque<Instant> windows;
    private int maxLimit;
    private Duration rollingInterval;

    public FixWindowCounterAlgorithm(int maxLimit, Duration rollingInterval) {
        this.windows = new ArrayDeque<>();
        this.maxLimit = maxLimit;
        this.rollingInterval = rollingInterval;
    }

    /**
     * true indicating allow or false
     *
     * @param timestamp
     * @return
     */
    public synchronized boolean addRequest(Instant timestamp) {
        if (!windows.isEmpty()) {
            Instant windowStart = windows.peekFirst();
            Duration elapsed = Duration.between(windowStart, timestamp);
            if (elapsed.compareTo(rollingInterval) >= 0) {
                windows.clear();
            }
        }

        if (windows.size() >= maxLimit) {
            return false;
        }

        windows.addLast(timestamp);
        return true;
    }
}
