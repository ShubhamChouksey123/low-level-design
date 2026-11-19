package com.shubham.app.logging.ideal.entity;

import com.shubham.app.logging.ideal.service.TraceContext;

import java.time.Instant;
import java.util.List;

public class Logger {

    private final String name;
    private final LogLevel threshold;
    private final List<Appender> appenders;

    public Logger(String name, LogLevel threshold, List<Appender> appenders) {
        this.name = name;
        this.threshold = threshold;
        this.appenders = appenders;
    }

    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public boolean isEnabled(LogLevel level) {
        return level.isEnabledFor(threshold);
    }

    public void log(LogLevel level, String message) {
        if (!isEnabled(level)) {
            return;
        }
        // Thread name and trace id are read here, on the caller's thread: an async
        // appender runs on a
        // different thread, where both would be wrong.
        LogEvent event = new LogEvent(Instant.now(), level, name, Thread.currentThread().getName(), TraceContext.get(),
                message);
        for (Appender appender : appenders) {
            try {
                appender.append(event);
            } catch (RuntimeException e) {
                // One broken destination must never stop the others, or the caller.
                System.err.println("Appender " + appender.getClass().getSimpleName() + " failed: " + e);
            }
        }
    }

    public String getName() {
        return name;
    }
}
