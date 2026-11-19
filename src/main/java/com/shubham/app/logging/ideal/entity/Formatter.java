package com.shubham.app.logging.ideal.entity;

/**
 * Turns a {@link LogEvent} into the text an appender writes. Kept separate so
 * layout is not tied to a destination.
 */
public interface Formatter {

    String format(LogEvent event);
}
