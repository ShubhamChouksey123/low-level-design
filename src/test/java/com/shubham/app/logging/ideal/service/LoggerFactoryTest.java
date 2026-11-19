package com.shubham.app.logging.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.LogEvent;
import com.shubham.app.logging.ideal.entity.LogLevel;
import com.shubham.app.logging.ideal.entity.Logger;
import com.shubham.app.logging.ideal.exception.InvalidLoggingConfigException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerFactoryTest {

    private static class RecordingAppender implements Appender {
        final List<LogEvent> events = new ArrayList<>();
        boolean closed;

        @Override
        public void append(LogEvent event) {
            events.add(event);
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    @Test
    void sameNameGivesTheSameLoggerAndTheClassNameBecomesTheLoggerName() {
        LoggerFactory factory = new LoggerFactory(LogLevel.INFO, List.of(new RecordingAppender()));

        assertSame(factory.getLogger(LoggerFactoryTest.class), factory.getLogger(LoggerFactoryTest.class));
        assertEquals(LoggerFactoryTest.class.getName(), factory.getLogger(LoggerFactoryTest.class).getName());
    }

    @Test
    void loggersFromOneFactoryShareThresholdAndAppenders() {
        RecordingAppender appender = new RecordingAppender();
        LoggerFactory factory = new LoggerFactory(LogLevel.WARN, List.of(appender));
        Logger a = factory.getLogger("a");
        Logger b = factory.getLogger("b");

        a.info("filtered");
        a.warn("from a");
        b.error("from b");

        assertEquals(2, appender.events.size());
        assertEquals("a", appender.events.get(0).loggerName());
        assertEquals("b", appender.events.get(1).loggerName());
    }

    @Test
    void shutdownClosesEveryAppender() {
        RecordingAppender first = new RecordingAppender();
        RecordingAppender second = new RecordingAppender();

        new LoggerFactory(LogLevel.INFO, List.of(first, second)).shutdown();

        assertTrue(first.closed && second.closed);
    }

    @Test
    void rejectsMissingThresholdAndMissingOrNullAppenders() {
        List<Appender> one = List.of(new RecordingAppender());
        assertThrows(InvalidLoggingConfigException.class, () -> new LoggerFactory(null, one));
        assertThrows(InvalidLoggingConfigException.class, () -> new LoggerFactory(LogLevel.INFO, null));
        assertThrows(InvalidLoggingConfigException.class, () -> new LoggerFactory(LogLevel.INFO, List.of()));
        assertThrows(InvalidLoggingConfigException.class,
                () -> new LoggerFactory(LogLevel.INFO, Arrays.asList(new RecordingAppender(), null)));
    }
}
