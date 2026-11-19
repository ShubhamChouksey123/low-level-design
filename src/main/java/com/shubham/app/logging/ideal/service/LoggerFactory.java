package com.shubham.app.logging.ideal.service;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.LogLevel;
import com.shubham.app.logging.ideal.entity.Logger;
import com.shubham.app.logging.ideal.exception.InvalidLoggingConfigException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hands out one {@link Logger} per name, all sharing the same threshold and
 * appenders. Loggers are named after the class that asks for them, which is
 * where the "class name" log field comes from.
 */
public class LoggerFactory {

    private final LogLevel threshold;
    private final List<Appender> appenders;
    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    public LoggerFactory(LogLevel threshold, List<Appender> appenders) {
        if (threshold == null) {
            throw new InvalidLoggingConfigException("threshold is required");
        }
        if (appenders == null || appenders.isEmpty() || appenders.stream().anyMatch(Objects::isNull)) {
            throw new InvalidLoggingConfigException("at least one non-null appender is required");
        }
        this.threshold = threshold;
        this.appenders = List.copyOf(appenders);
    }

    public Logger getLogger(Class<?> owner) {
        return getLogger(owner.getName());
    }

    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, n -> new Logger(n, threshold, appenders));
    }

    /**
     * Closes every appender, which also drains and flushes the async ones. Call
     * once, at shutdown.
     */
    public void shutdown() {
        for (Appender appender : appenders) {
            try {
                appender.close();
            } catch (RuntimeException e) {
                System.err.println("Closing " + appender.getClass().getSimpleName() + " failed: " + e);
            }
        }
    }
}
