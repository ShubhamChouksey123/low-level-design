package com.shubham.app.logging.interview.service;

import com.shubham.app.logging.interview.entity.CustomLogger;
import com.shubham.app.logging.interview.entity.LogLevel;

import java.util.logging.Logger;

// import

public class LogService {

    private LogLevel level;
    private CustomLogger logger;
    private Logger LOGGER_SLF4J = Logger.getLogger(this.getClass().getName());

    public LogService(LogLevel level, CustomLogger logger) {
        this.level = level;
        this.logger = logger;
    }

    /**
     * Producer => LogService
     *
     * <p>
     * onMessage(level, message)
     *
     * <p>
     * Consumer list of consumers list of queue
     *
     * <p>
     * // timestamp, level, message, traceId, class name, thread name
     *
     * <p>
     * 1. How will message in queue will look ? 2. method definition onMessage
     *
     * <p>
     * onMessage(queueMessage) => timestamp => known => from channel id => class
     * name from threadId => thread name, requestContext
     *
     * <p>
     * Queue => { level, message, tread-Id }
     */
    public void printMessage(LogLevel logLevel, String message) {

        // System.out.println("provided log level : " + logLevel);
        // System.out.println("config log level : " + level);
        if (logLevel.getId() < level.getId()) {
            return;
        }

        logger.print(logLevel, message);
    }
}
