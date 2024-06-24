package com.shubham.app.loggingsystem.entity;

public enum LoggingLevel {
    TRACE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4);

    private final int level;

    LoggingLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
