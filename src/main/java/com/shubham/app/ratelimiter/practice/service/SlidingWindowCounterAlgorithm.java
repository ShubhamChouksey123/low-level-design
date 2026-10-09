package com.shubham.app.ratelimiter.practice.service;

import com.shubham.app.ratelimiter.practice.entity.RateLimitAlgorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowCounterAlgorithm implements RateLimitAlgorithm {

    private Deque<Instant> windows;
    private int maxRequestsPerWindow;
    private Duration windowSize;

    public SlidingWindowCounterAlgorithm(int maxRequestsPerWindow, Duration windowSize) {
        this.windows = new ArrayDeque<>();
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSize = windowSize;
    }

    public boolean addRequest(Instant timestamp) {

        while (!windows.isEmpty()) {

            Instant startTimestamp = windows.peekFirst();
            Duration elaspedDuration = Duration.between(startTimestamp, timestamp);

            if (elaspedDuration.compareTo(windowSize) < 0) {
                break;
            }
            // else, remove the first element
            windows.pollFirst();
        }

        // drop the request if quoto is reached
        if (windows.size() >= maxRequestsPerWindow)
            return false;

        windows.offerLast(timestamp);
        return true;
    }
}
