package com.shubham.app.logging.ideal.entity;

/** The one thing that varies: where a finished log event goes. */
public interface Appender {

    void append(LogEvent event);

    /**
     * Pushes anything buffered inside this appender to its destination. No-op by
     * default.
     */
    default void flush() {
    }

    /** Flushes and releases resources. No-op by default. */
    default void close() {
    }
}
