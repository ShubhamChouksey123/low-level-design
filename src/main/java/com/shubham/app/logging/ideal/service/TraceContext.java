package com.shubham.app.logging.ideal.service;

/**
 * Per-thread trace id, set by request-handling code and read by {@code Logger}
 * when an event is created.
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceContext() {
    }

    public static void set(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String get() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
