package com.shubham.app.loggingsystem.entity;

public abstract class LogProcessor {

    public LogProcessor nextLogProcessor;

    public LogProcessor(LogProcessor nextLogProcessor) {
        if (nextLogProcessor != null) {
            this.nextLogProcessor = nextLogProcessor;
        }
    }

    public abstract void write(LoggingLevel loggingLevel, String content);
}
