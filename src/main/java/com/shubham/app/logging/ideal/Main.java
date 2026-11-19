package com.shubham.app.logging.ideal;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.Formatter;
import com.shubham.app.logging.ideal.entity.LogLevel;
import com.shubham.app.logging.ideal.entity.Logger;
import com.shubham.app.logging.ideal.service.AsyncAppender;
import com.shubham.app.logging.ideal.service.ConsoleAppender;
import com.shubham.app.logging.ideal.service.FileAppender;
import com.shubham.app.logging.ideal.service.LoggerFactory;
import com.shubham.app.logging.ideal.service.OverflowPolicy;
import com.shubham.app.logging.ideal.service.PatternFormatter;
import com.shubham.app.logging.ideal.service.TraceContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Path logFile = Files.createTempFile("ideal-logging-demo", ".log");
        Formatter formatter = new PatternFormatter();

        // Console is written synchronously; the file goes through the async decorator,
        // which buffers
        // writes and flushes every 100 events or every 200 ms, whichever comes first.
        Appender console = new ConsoleAppender(formatter);
        Appender file = new AsyncAppender(new FileAppender(logFile, formatter), 1_000, 100, Duration.ofMillis(200),
                OverflowPolicy.BLOCK);
        LoggerFactory factory = new LoggerFactory(LogLevel.INFO, List.of(console, file));

        Logger logger = factory.getLogger(Main.class);
        TraceContext.set("req-42");
        logger.debug("filtered out: below the INFO threshold");
        logger.info("order accepted");
        logger.warn("inventory low");
        logger.error("payment failed");
        TraceContext.clear();

        // Closing drains the queue and flushes the file, so nothing buffered is lost on
        // a clean exit.
        factory.shutdown();

        System.out.println("--- contents of " + logFile + " ---");
        Files.readAllLines(logFile).forEach(System.out::println);
    }
}
