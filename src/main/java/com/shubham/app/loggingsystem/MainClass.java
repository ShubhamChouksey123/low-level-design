package com.shubham.app.loggingsystem;

import com.shubham.app.loggingsystem.entity.*;
import com.shubham.app.loggingsystem.service.*;

public class MainClass {

    public static void main(String[] args) {
        LogProcessor logProcessor = new TraceLogProcessor(
                new DebugLogProcessor(new InfoLogProcessor(new WarnLogProcessor(new ErrorLogProcessor(null)))));

        logProcessor.write(LoggingLevel.DEBUG, "we have debug started the main method");
        logProcessor.write(LoggingLevel.INFO, "we have info started the main method");
        logProcessor.write(LoggingLevel.WARN, "we have warn started the main method");
    }
}
