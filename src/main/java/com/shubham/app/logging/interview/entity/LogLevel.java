package com.shubham.app.logging.interview.entity;

public enum LogLevel {
    TRACE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4);

    int id;

    LogLevel(int i) {
        this.id = i;
    }

    public int getId() {
        return id;
    }
}
