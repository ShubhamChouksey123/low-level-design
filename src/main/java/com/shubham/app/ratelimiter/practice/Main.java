package com.shubham.app.ratelimiter.practice;

import com.shubham.app.ratelimiter.practice.service.FixedWindowRateLimitAlgorithmFactory;
import com.shubham.app.ratelimiter.practice.service.RateLimiterService;
import com.shubham.app.ratelimiter.practice.service.SlidingWindowCounterAlgorithmFactory;
import com.shubham.app.ratelimiter.practice.service.TokenBucketRateLimitAlgorithmFactory;

import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {

        System.out.println("hello");

        RateLimiterService rateLimiterService = new RateLimiterService(
                new FixedWindowRateLimitAlgorithmFactory(3, Duration.ofSeconds(10)));

        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:01Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:02Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:03Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:04Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:09Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:10Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:11Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:12Z")));

        System.out.println("New Type of requests\n");
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:31.950Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:31.960Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:31.970Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:32.100Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:32.200Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:32.950Z")));
        System.out.println(
                "status :" + rateLimiterService.checkRateLimitStatus(2, Instant.parse("2026-10-08T22:05:32.951Z")));

        System.out.println("Sliding window Type of requests\n");
        RateLimiterService rateLimiterSlidingWindowService = new RateLimiterService(
                new SlidingWindowCounterAlgorithmFactory(3, Duration.ofSeconds(10)));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:01Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:02Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:08Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:09Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:10Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:11Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:12Z")));
        System.out.println("status :"
                + rateLimiterSlidingWindowService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:13Z")));

        System.out.println("Token Bucket Type of requests\n");
        RateLimiterService rateLimiterTokenBucketService = new RateLimiterService(
                new TokenBucketRateLimitAlgorithmFactory(3, 1));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:00.010Z")));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:00.020Z")));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:00.030Z")));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:00.040Z")));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:00.080Z")));
        System.out.println("status :"
                + rateLimiterTokenBucketService.checkRateLimitStatus(1, Instant.parse("2026-10-08T22:05:01.010Z")));
    }
}
