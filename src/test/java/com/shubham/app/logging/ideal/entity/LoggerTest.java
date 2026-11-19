package com.shubham.app.logging.ideal.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.shubham.app.logging.ideal.service.TraceContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

    private static class RecordingAppender implements Appender {
        private final List<LogEvent> events = new ArrayList<>();

        @Override
        public void append(LogEvent event) {
            events.add(event);
        }
    }

    private static class FailingAppender implements Appender {
        @Override
        public void append(LogEvent event) {
            throw new IllegalStateException("destination is down");
        }
    }

    @AfterEach
    void clearTrace() {
        TraceContext.clear();
    }

    @Test
    void messagesBelowTheThresholdAreNeverSentToAnAppender() {
        RecordingAppender appender = new RecordingAppender();
        Logger logger = new Logger("test", LogLevel.INFO, List.of(appender));

        logger.trace("t");
        logger.debug("d");
        logger.info("i");
        logger.warn("w");
        logger.error("e");

        assertEquals(List.of(LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR),
                appender.events.stream().map(LogEvent::level).toList());
        assertFalse(logger.isEnabled(LogLevel.DEBUG));
        assertTrue(logger.isEnabled(LogLevel.INFO));
    }

    @Test
    void eventCarriesLoggerNameThreadNameAndTimestamp() {
        RecordingAppender appender = new RecordingAppender();
        Logger logger = new Logger("com.example.Foo", LogLevel.TRACE, List.of(appender));

        logger.info("hello");

        LogEvent event = appender.events.get(0);
        assertEquals("com.example.Foo", event.loggerName());
        assertEquals(Thread.currentThread().getName(), event.threadName());
        assertEquals("hello", event.message());
        assertNotNull(event.timestamp());
    }

    @Test
    void traceIdIsReadFromTheCallingThreadAndIsNullWhenUnset() {
        RecordingAppender appender = new RecordingAppender();
        Logger logger = new Logger("test", LogLevel.TRACE, List.of(appender));

        logger.info("before");
        TraceContext.set("req-1");
        logger.info("during");
        TraceContext.clear();
        logger.info("after");

        assertNull(appender.events.get(0).traceId());
        assertEquals("req-1", appender.events.get(1).traceId());
        assertNull(appender.events.get(2).traceId());
    }

    @Test
    void everyAppenderReceivesTheSameEvent() {
        RecordingAppender first = new RecordingAppender();
        RecordingAppender second = new RecordingAppender();
        Logger logger = new Logger("test", LogLevel.TRACE, List.of(first, second));

        logger.info("fan out");

        assertEquals(1, first.events.size());
        assertEquals(first.events.get(0), second.events.get(0));
    }

    @Test
    void aFailingAppenderDoesNotStopTheOthersOrTheCaller() {
        RecordingAppender healthy = new RecordingAppender();
        Logger logger = new Logger("test", LogLevel.TRACE, List.of(new FailingAppender(), healthy));

        logger.info("still delivered");

        assertEquals(1, healthy.events.size());
    }
}
