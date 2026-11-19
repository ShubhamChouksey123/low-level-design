package com.shubham.app.logging.ideal.entity;

public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR;

    /**
     * True when a message at this level should be emitted by a logger set to
     * {@code threshold}.
     */
    public boolean isEnabledFor(LogLevel threshold) {
        return compareTo(threshold) >= 0;
    }
}
