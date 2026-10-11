package com.shubham.app.lru.practice.exception;

public class InvalidCapacityException extends RuntimeException {
    public InvalidCapacityException(String message) {
        super(message);
    }
}
