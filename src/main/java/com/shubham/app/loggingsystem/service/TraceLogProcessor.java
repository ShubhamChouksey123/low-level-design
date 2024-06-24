package com.shubham.app.loggingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.loggingsystem.entity.LogProcessor;
import com.shubham.app.loggingsystem.entity.LoggingLevel;

import java.util.Objects;

public class TraceLogProcessor extends LogProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public TraceLogProcessor(LogProcessor nextLogProcessor) {
        super(nextLogProcessor);
    }

    @Override
    public void write(LoggingLevel loggingLevel, String content) {
        if (Objects.equals(loggingLevel, LoggingLevel.TRACE)) {
            // logger.trace(" --- TRACE --- {}", content);
            System.out.println("  --- TRACE ---  " + content);
            return;
        }
        if (nextLogProcessor != null) {
            super.nextLogProcessor.write(loggingLevel, content);
        }
    }
}
