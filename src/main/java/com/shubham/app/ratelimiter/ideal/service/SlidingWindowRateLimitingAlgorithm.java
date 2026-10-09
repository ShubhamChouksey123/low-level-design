package com.shubham.app.ratelimiter.ideal.service;

import com.shubham.app.ratelimiter.ideal.entity.RateLimitingAlgorithm;
import com.shubham.app.ratelimiter.ideal.exception.InvalidRateLimiterConfigException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sliding window log: every *allowed* request's timestamp is kept in the log.
 * On each new request, timestamps older than {@code windowSize} are evicted
 * from the front first, then the remaining count decides allow/reject - so the
 * window is always exactly "the last {@code windowSize}", with no reset
 * boundary a burst can exploit.
 *
 * <p>
 * A rejected request is never logged - it never happened from the service's
 * point of view, so it can't consume quota that an actual accepted request
 * would need.
 */
public class SlidingWindowRateLimitingAlgorithm implements RateLimitingAlgorithm {

    private final int maxRequestsPerWindow;
    private final Duration windowSize;
    private final Deque<Instant> windowRequests = new ArrayDeque<>();

    public SlidingWindowRateLimitingAlgorithm(int maxRequestsPerWindow, Duration windowSize) {
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
    public boolean allowRequest(Instant timestamp) {
        while (!windowRequests.isEmpty()) {
            Instant oldest = windowRequests.peekFirst();
            if (Duration.between(oldest, timestamp).compareTo(windowSize) < 0) {
                break;
            }
            windowRequests.pollFirst();
        }

        if (windowRequests.size() >= maxRequestsPerWindow) {
            return false;
        }

        windowRequests.addLast(timestamp);
        return true;
    }
}
