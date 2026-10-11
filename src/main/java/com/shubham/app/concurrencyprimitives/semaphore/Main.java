package com.shubham.app.concurrencyprimitives.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        demonstrateBoundedConcurrentAccess();
        demonstrateAcquireBlocksUntilAPermitIsReleased();
        demonstrateTryAcquireDoesNotBlock();
    }

    // Far more threads than permits all hit the pool at once. The semaphore's
    // whole job is making sure "how many are checked out right now" never
    // exceeds the permit count, no matter how many threads are waiting.
    private static void demonstrateBoundedConcurrentAccess() throws InterruptedException {
        System.out.println("--- bounded concurrent access ---");
        int maxConnections = 3;
        ConnectionPool pool = new ConnectionPool(maxConnections);

        ExecutorService workers = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            workers.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        pool.acquire();
                        Thread.sleep(50); // simulate using the connection
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        pool.release();
                    }
                }
            });
        }
        workers.shutdown();
        workers.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("permits available: " + maxConnections + ", peak concurrent holders (expected <= "
                + maxConnections + "): " + pool.getPeakActiveConnections());
    }

    // One thread checks out every permit; a second thread's acquire() blocks
    // until the first thread releases one - it doesn't fail, it waits.
    private static void demonstrateAcquireBlocksUntilAPermitIsReleased() throws InterruptedException {
        System.out.println("--- acquire() blocks until a permit frees up ---");
        ConnectionPool pool = new ConnectionPool(1);
        pool.acquire(); // the only permit is now held

        Thread releaser = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("releaser: releasing the only permit now");
                pool.release();
            }
        });
        releaser.start();

        long start = System.currentTimeMillis();
        pool.acquire(); // blocks here until the releaser thread calls release()
        long waitedMillis = System.currentTimeMillis() - start;
        System.out.println("acquired after waiting ~" + waitedMillis + "ms (expected >= 300ms)");

        pool.release();
        releaser.join();
    }

    // Same setup, but this caller isn't willing to wait at all - tryAcquire()
    // returns false immediately instead of blocking.
    private static void demonstrateTryAcquireDoesNotBlock() throws InterruptedException {
        System.out.println("--- tryAcquire() does not block ---");
        ConnectionPool pool = new ConnectionPool(1);
        pool.acquire(); // the only permit is now held, and never released below

        boolean acquired = pool.tryAcquire();
        System.out.println("tryAcquire() while the only permit is held (expected false): " + acquired);
    }
}
