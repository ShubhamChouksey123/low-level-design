package com.shubham.app.ratelimiter.ideal.service;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Fixed window: a window opens on the first request after a reset and stays
 * open for exactly {@code
 * windowSize}. Every request inside that window shares the same count; once the
 * window's elapsed time reaches {@code windowSize}, the whole window resets and
 * the count starts over - so a burst can land right at a window boundary and be
 * followed immediately by a fresh quota.
 */
public class FixedWindowRateLimitingAlgorithm implements RateLimitingAlgorithm {

    private final int maxRequestsPerWindow;
    private final Duration windowSize;
    private final Deque<Instant> windowRequests = new ArrayDeque<>();

    public FixedWindowRateLimitingAlgorithm(int maxRequestsPerWindow, Duration windowSize) {
        if (maxRequestsPerWindow <= 0) {
            throw new InvalidRateLimiterConfigException(
                    "maxRequestsPerWindow must be positive: " + maxRequestsPerWindow);
        }
        if (windowSize == null || windowSize.compareTo(Duration.ZERO) <= 0) {
            throw new InvalidRateLimiterConfigException("windowSize must be positive: " + windowSize);
        }
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSize = windowSize;
    }

    @Override
    public synchronized boolean allowRequest(Instant timestamp) {
        if (!windowRequests.isEmpty()) {
            Instant windowStart = windowRequests.peekFirst();
            if (Duration.between(windowStart, timestamp).compareTo(windowSize) >= 0) {
                windowRequests.clear();
            }
        }

        if (windowRequests.size() >= maxRequestsPerWindow) {
            return false;
        }

        windowRequests.addLast(timestamp);
        return true;
    }
}
