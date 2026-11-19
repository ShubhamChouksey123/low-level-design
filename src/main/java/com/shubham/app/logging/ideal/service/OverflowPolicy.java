package com.shubham.app.logging.ideal.service;

/** What {@link AsyncAppender#append} does when its queue is full. */
public enum OverflowPolicy {
    /**
     * Make the caller wait: no log is lost, but a slow destination slows the
     * application.
     */
    BLOCK,
    /**
     * Drop the new event and count it: the application never waits, but logs can be
     * lost.
     */
    DROP_NEWEST
}
