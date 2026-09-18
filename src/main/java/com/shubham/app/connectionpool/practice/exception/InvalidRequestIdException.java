package com.shubham.app.connectionpool.practice.exception;

public class InvalidRequestIdException extends RuntimeException {
    public InvalidRequestIdException(String message) {
        super(message);
    }
}
