package com.shubham.app.logging.interview;

import com.shubham.app.logging.interview.entity.ConsoleAppender;
import com.shubham.app.logging.interview.entity.CustomLogger;
import com.shubham.app.logging.interview.entity.LogLevel;
import com.shubham.app.logging.interview.service.LogService;

// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        CustomLogger customLogger = new ConsoleAppender();
        LogService logService = new LogService(LogLevel.INFO, customLogger);

        logService.printMessage(LogLevel.DEBUG, "debug message");
        logService.printMessage(LogLevel.WARN, "warn message");
    }
}
