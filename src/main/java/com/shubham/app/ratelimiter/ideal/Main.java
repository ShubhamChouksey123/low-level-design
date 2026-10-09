package com.shubham.app.ratelimiter.ideal;

import com.shubham.app.ratelimiter.ideal.entity.RateLimiter;
import com.shubham.app.ratelimiter.ideal.service.FixedWindowRateLimitingAlgorithmFactory;
import com.shubham.app.ratelimiter.ideal.service.SlidingWindowRateLimitingAlgorithmFactory;
import com.shubham.app.ratelimiter.ideal.service.TokenBucketRateLimitingAlgorithmFactory;

import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        int[] offsetsSeconds = {0, 2, 5, 7, 11};

        RateLimiter fixedWindowLimiter = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(3, Duration.ofSeconds(10)));
        RateLimiter slidingWindowLimiter = new RateLimiter(
                new SlidingWindowRateLimitingAlgorithmFactory(3, Duration.ofSeconds(10)));

        System.out.println("Fixed window (N=3, 10s window), client \"user1\":");
        for (int offsetSeconds : offsetsSeconds) {
            boolean allowed = fixedWindowLimiter.allowRequest("user1", base.plusSeconds(offsetSeconds));
            System.out.println("  t=" + offsetSeconds + "s -> " + (allowed ? "allowed" : "rejected"));
        }

        System.out.println("Sliding window (N=3, 10s window), same requests:");
        for (int offsetSeconds : offsetsSeconds) {
            boolean allowed = slidingWindowLimiter.allowRequest("user1", base.plusSeconds(offsetSeconds));
            System.out.println("  t=" + offsetSeconds + "s -> " + (allowed ? "allowed" : "rejected"));
        }

        // The classic fixed-window edge-burst problem: 3 requests land late in a
        // window, then 3
        // more land just after it resets. Fixed window wrongly allows all 6 - its reset
        // wipes
        // out the whole count, forgetting the late-window requests are still only
        // fractions of a
        // second old. Sliding window never resets wholesale, so it correctly caps this
        // at 4.
        RateLimiter burstFixedWindowLimiter = new RateLimiter(
                new FixedWindowRateLimitingAlgorithmFactory(3, Duration.ofSeconds(10)));
        RateLimiter burstSlidingWindowLimiter = new RateLimiter(
                new SlidingWindowRateLimitingAlgorithmFactory(3, Duration.ofSeconds(10)));
        long[] burstOffsetsMillis = {0, 9_800, 9_900, 10_000, 10_050, 10_100};

        System.out.println("Edge burst (N=3, 10s window), client \"user1\":");
        System.out.println("Fixed window:");
        for (long offsetMillis : burstOffsetsMillis) {
            boolean allowed = burstFixedWindowLimiter.allowRequest("user1", base.plusMillis(offsetMillis));
            System.out.println("  t=" + (offsetMillis / 1000.0) + "s -> " + (allowed ? "allowed" : "rejected"));
        }
        System.out.println("Sliding window:");
        for (long offsetMillis : burstOffsetsMillis) {
            boolean allowed = burstSlidingWindowLimiter.allowRequest("user1", base.plusMillis(offsetMillis));
            System.out.println("  t=" + (offsetMillis / 1000.0) + "s -> " + (allowed ? "allowed" : "rejected"));
        }

        // The exact bucketCapacity=3/rate=1-per-second scenario that exposed the
        // Math.max/
        // Math.min inversion in the practice/ code (that bug let all 6 requests
        // through). Here:
        // 3 initial tokens cover the first 3 calls (10/20/30ms apart), the 4th and 5th
        // are
        // rejected since well under a second has passed, and the 6th (930ms after the
        // 5th) is
        // allowed again once ~0.93 tokens refill on top of the ~0.07 left over.
        RateLimiter tokenBucketLimiter = new RateLimiter(new TokenBucketRateLimitingAlgorithmFactory(3, 1.0));
        long[] tokenBucketOffsetsMillis = {10, 20, 30, 40, 80, 1_010};

        System.out.println("Token bucket (capacity=3, 1 token/s), client \"user1\":");
        for (long offsetMillis : tokenBucketOffsetsMillis) {
            boolean allowed = tokenBucketLimiter.allowRequest("user1", base.plusMillis(offsetMillis));
            System.out.println("  t=" + (offsetMillis / 1000.0) + "s -> " + (allowed ? "allowed" : "rejected"));
        }
    }
}
