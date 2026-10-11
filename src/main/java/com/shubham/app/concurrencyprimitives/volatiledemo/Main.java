package com.shubham.app.concurrencyprimitives.volatiledemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        demonstrateVisibilityOfAVolatileFlag();
        demonstrateVolatileIsNotAtomicForCompoundOperations();
    }

    // Two threads touch worker.running:
    // - writer thread: whichever thread runs this method (the JVM's "main"
    // thread) - it calls worker.stop(), the only write to running.
    // - reader thread: workerThread, started below - it's the one actually
    // executing run()'s while (running) loop, reading running on every
    // iteration.
    // volatile is what guarantees workerThread's read of running sees the
    // main thread's write promptly, instead of possibly looping on a stale
    // cached value forever.
    private static void demonstrateVisibilityOfAVolatileFlag() throws InterruptedException {
        System.out.println("--- volatile: a flag write becomes visible to another thread ---");
        StoppableWorker worker = new StoppableWorker();
        Thread workerThread = new Thread(worker); // the reader thread
        workerThread.start();

        Thread.sleep(100); // let the reader thread (workerThread) spin for a bit
        worker.stop(); // the writer thread (main) writes running = false here

        workerThread.join(1000); // should not need anywhere close to this long
        System.out.println("worker thread stopped (expected true): " + !workerThread.isAlive());
        System.out.println("iterations completed before stopping: " + worker.getIterations());
    }

    // 10 threads x 200,000 increments each = 2,000,000 expected. volatile
    // makes every read see the latest write, but count++ is still three
    // separate steps (read, add, write) - two threads can interleave between
    // them and one increment gets silently lost. High enough contention
    // (lots of threads, lots of increments, no sleeps in between) makes this
    // race reliably show up as a final count lower than 2,000,000, proving
    // volatile alone was not enough here.
    private static void demonstrateVolatileIsNotAtomicForCompoundOperations() throws InterruptedException {
        System.out.println("--- volatile: NOT atomic for a compound read-modify-write ---");
        VolatileCounter counter = new VolatileCounter();
        int threads = 10;
        int incrementsPerThread = 200_000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        int expected = threads * incrementsPerThread;
        System.out.println("expected " + expected + ", got " + counter.get()
                + " (a lower number proves lost updates - volatile did not make count++ atomic)");
    }
}
