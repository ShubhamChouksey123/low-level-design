package com.shubham.app.logging.ideal.service;

import com.shubham.app.logging.ideal.entity.Formatter;
import com.shubham.app.logging.ideal.entity.LogEvent;

/**
 * {@code <timestamp> [LEVEL] [thread] [trace=<id>] <logger> - <message>}; the
 * trace part is omitted when unset.
 */
public class PatternFormatter implements Formatter {

    @Override
    public String format(LogEvent event) {
        StringBuilder line = new StringBuilder();
        line.append(event.timestamp()).append(" [").append(event.level()).append("] [").append(event.threadName())
                .append("] ");
        if (event.traceId() != null) {
            line.append("[trace=").append(event.traceId()).append("] ");
        }
        return line.append(event.loggerName()).append(" - ").append(event.message()).toString();
    }
}
