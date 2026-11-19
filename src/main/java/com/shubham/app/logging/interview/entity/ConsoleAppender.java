package com.shubham.app.logging.interview.entity;

import java.time.Instant;

public class ConsoleAppender implements CustomLogger {

    // channel => Class name

    @Override
    public void print(LogLevel logLevel, String message) {

        System.out.print(Instant.now() + " " + logLevel.name() + " ");
        System.out.print("Message from Console appender : " + message + "\n");
    }
}
