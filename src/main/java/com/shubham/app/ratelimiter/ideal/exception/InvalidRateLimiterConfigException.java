package com.shubham.app.ratelimiter.ideal.exception;

public class InvalidRateLimiterConfigException extends RuntimeException {

    public InvalidRateLimiterConfigException(String message) {
        super(message);
    }
}
