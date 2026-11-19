package com.shubham.app.logging.ideal.service;

import com.shubham.app.logging.ideal.entity.Appender;
import com.shubham.app.logging.ideal.entity.Formatter;
import com.shubham.app.logging.ideal.entity.LogEvent;

import java.io.PrintStream;

public class ConsoleAppender implements Appender {

    private final Formatter formatter;
    private final PrintStream out;

    public ConsoleAppender(Formatter formatter) {
        this(formatter, System.out);
    }

    public ConsoleAppender(Formatter formatter, PrintStream out) {
        this.formatter = formatter;
        this.out = out;
    }

    @Override
    public void append(LogEvent event) {
        // One println of one string: two separate prints from different threads could
        // interleave.
        out.println(formatter.format(event));
    }
}
