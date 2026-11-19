package com.shubham.app.logging.ideal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.shubham.app.logging.ideal.entity.LogEvent;
import com.shubham.app.logging.ideal.entity.LogLevel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileAppenderTest {

    @TempDir
    Path dir;

    private LogEvent event(String message) {
        return new LogEvent(Instant.EPOCH, LogLevel.INFO, "logger", "main", null, message);
    }

    @Test
    void writesOneLinePerEventAfterFlush() throws IOException {
        Path file = dir.resolve("app.log");
        FileAppender appender = new FileAppender(file, new PatternFormatter());

        appender.append(event("first"));
        appender.append(event("second"));
        appender.flush();

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).endsWith("logger - first"));
        assertTrue(lines.get(1).endsWith("logger - second"));
        appender.close();
    }

    @Test
    void nothingIsGuaranteedOnDiskUntilFlush() throws IOException {
        Path file = dir.resolve("app.log");
        FileAppender appender = new FileAppender(file, new PatternFormatter());

        appender.append(event("buffered"));

        assertEquals(0, Files.size(file));
        appender.close();
        assertEquals(1, Files.readAllLines(file).size());
    }

    @Test
    void reopeningTheSameFileAppendsInsteadOfOverwriting() throws IOException {
        Path file = dir.resolve("app.log");
        FileAppender first = new FileAppender(file, new PatternFormatter());
        first.append(event("old"));
        first.close();

        FileAppender second = new FileAppender(file, new PatternFormatter());
        second.append(event("new"));
        second.close();

        assertEquals(2, Files.readAllLines(file).size());
    }
}
