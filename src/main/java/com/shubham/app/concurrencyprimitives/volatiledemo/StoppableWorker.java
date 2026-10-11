package com.shubham.app.concurrencyprimitives.volatiledemo;

// Same shape as the running-flag example in concepts/concurrency-primitives.md:
// one thread writes the flag, others only ever read it - exactly the pattern
// volatile is for.
public class StoppableWorker implements Runnable {

    private volatile boolean running = true;
    private volatile long iterations = 0;

    @Override
    public void run() {
        while (running) {
            iterations++;
        }
    }

    // Called from a different thread than run() - volatile is what guarantees
    // this write is visible to the worker thread immediately, instead of the
    // worker possibly spinning forever on a cached stale value.
    public void stop() {
        running = false;
    }

    public long getIterations() {
        return iterations;
    }
}
