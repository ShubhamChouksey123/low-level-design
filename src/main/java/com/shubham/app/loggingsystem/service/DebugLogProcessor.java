package com.shubham.app.loggingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.loggingsystem.entity.LogProcessor;
import com.shubham.app.loggingsystem.entity.LoggingLevel;

import java.util.Objects;

public class DebugLogProcessor extends LogProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public DebugLogProcessor(LogProcessor nextLogProcessor) {
        super(nextLogProcessor);
    }

    @Override
    public void write(LoggingLevel loggingLevel, String content) {

        if (Objects.equals(loggingLevel, LoggingLevel.DEBUG)) {
            // logger.debug(" --- DEBUG --- {}", content);
            System.out.println("  --- DEBUG ---  " + content);
            return;
        }
        if (nextLogProcessor != null) {
            super.nextLogProcessor.write(loggingLevel, content);
        }
    }
}
