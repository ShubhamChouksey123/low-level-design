package com.shubham.app.logging.ideal.service;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.Formatter;
import com.shubham.app.logging.ideal.entity.LogEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Writes through an in-memory buffer; nothing is guaranteed on disk until
 * {@link #flush()} or {@link #close()}.
 */
public class FileAppender implements Appender {

    private final Formatter formatter;
    private final BufferedWriter writer;

    public FileAppender(Path file, Formatter formatter) {
        this.formatter = formatter;
        try {
            this.writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot open log file " + file, e);
        }
    }

    @Override
    public synchronized void append(LogEvent event) {
        try {
            writer.write(formatter.format(event));
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
