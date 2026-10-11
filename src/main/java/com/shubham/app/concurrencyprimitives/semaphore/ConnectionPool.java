package com.shubham.app.concurrencyprimitives.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

// Same shape as the connection-pool example in concepts/concurrency-primitives.md:
// a fixed number of interchangeable resources, where the permit count tracks how
// many are checked out - not the internal state of any one resource.
public class ConnectionPool {

    private final Semaphore permits;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicInteger peakActiveConnections = new AtomicInteger(0);

    public ConnectionPool(int maxConnections) {
        this.permits = new Semaphore(maxConnections);
    }

    // Blocks if every permit is already checked out.
    public void acquire() throws InterruptedException {
        permits.acquire();
        int active = activeConnections.incrementAndGet();
        peakActiveConnections.getAndUpdate(new IntUnaryOperator() {
            @Override
            public int applyAsInt(int currentPeak) {
                return Math.max(currentPeak, active);
            }
        });
    }

    // Same as acquire(), but gives up instead of waiting if no permit is free.
    public boolean tryAcquire() {
        if (!permits.tryAcquire()) {
            return false;
        }
        activeConnections.incrementAndGet();
        return true;
    }

    public void release() {
        activeConnections.decrementAndGet();
        permits.release();
    }

    public int getPeakActiveConnections() {
        return peakActiveConnections.get();
    }
}
