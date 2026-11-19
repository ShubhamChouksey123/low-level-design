package com.shubham.app.logging.ideal.entity;

import java.time.Instant;

/**
 * Everything an appender needs to render one log line. Every field is captured
 * on the caller's thread at the moment of the call, so it stays correct even
 * when an appender runs later on another thread.
 *
 * @param traceId
 *            may be null when the caller never set one
 */
public record LogEvent(Instant timestamp, LogLevel level, String loggerName, String threadName, String traceId,
        String message) {
}
