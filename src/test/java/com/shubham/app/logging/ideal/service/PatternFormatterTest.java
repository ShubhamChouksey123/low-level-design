package com.shubham.app.logging.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.logging.ideal.entity.LogEvent;
import com.shubham.app.logging.ideal.entity.LogLevel;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternFormatterTest {

    private final PatternFormatter formatter = new PatternFormatter();
    private final Instant at = Instant.parse("2026-11-18T10:15:30Z");

    @Test
    void includesTraceIdWhenPresent() {
        LogEvent event = new LogEvent(at, LogLevel.WARN, "com.x.Foo", "main", "req-9", "low stock");

        assertEquals("2026-11-18T10:15:30Z [WARN] [main] [trace=req-9] com.x.Foo - low stock", formatter.format(event));
    }

    @Test
    void omitsTheTracePartWhenNoTraceIdWasSet() {
        LogEvent event = new LogEvent(at, LogLevel.INFO, "com.x.Foo", "main", null, "started");

        assertEquals("2026-11-18T10:15:30Z [INFO] [main] com.x.Foo - started", formatter.format(event));
    }
}
