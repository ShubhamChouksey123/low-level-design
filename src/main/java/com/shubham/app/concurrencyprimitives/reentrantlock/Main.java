package com.shubham.app.concurrencyprimitives.reentrantlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        demonstrateMutualExclusionUnderConcurrentWithdrawals();
        demonstrateTryLockDoesNotBlock();
        demonstrateTryLockWithTimeout();
        demonstrateReentrancy();
    }

    // Many threads hammer the same account; withdraw() blocks on the lock the
    // same way a synchronized method would, so the balance never goes
    // negative no matter how the threads interleave.
    private static void demonstrateMutualExclusionUnderConcurrentWithdrawals() throws InterruptedException {
        System.out.println("--- mutual exclusion under concurrent withdrawals ---");
        BankAccount account = new BankAccount(1000);

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    account.withdraw(60);
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        // 20 attempts x 60 each = 1200 requested, but only 1000 is available,
        // so at most 16 withdrawals (960) can succeed - the lock is what
        // guarantees no two threads ever both see enough balance at once.
        System.out.println("final balance (never negative): " + account.getBalance());
    }

    // One thread holds the lock for a while; a second thread calls tryLock()
    // and gets false back immediately instead of waiting - something
    // synchronized has no way to express.
    private static void demonstrateTryLockDoesNotBlock() throws InterruptedException {
        System.out.println("--- tryLock() does not block ---");
        BankAccount account = new BankAccount(1000);

        Thread holder = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    account.holdLockFor(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        holder.start();
        Thread.sleep(100); // let the holder actually acquire the lock first

        boolean withdrew = account.tryWithdrawWithoutWaiting(50);
        System.out.println("withdrew while lock was held (expected false): " + withdrew);

        holder.join();
    }

    // Same setup, but this time the caller is willing to wait up to a
    // timeout - long enough for the holder to release, so the withdrawal
    // succeeds instead of bailing out immediately.
    private static void demonstrateTryLockWithTimeout() throws InterruptedException {
        System.out.println("--- tryLock(timeout) waits, then succeeds ---");
        BankAccount account = new BankAccount(1000);

        Thread holder = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    account.holdLockFor(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        holder.start();
        Thread.sleep(100);

        boolean withdrew = account.tryWithdrawWithTimeout(50, 1000);
        System.out.println("withdrew after waiting up to 1s (expected true): " + withdrew);

        holder.join();
    }

    // "Reentrant" means a thread already holding the lock can lock() again
    // without deadlocking itself - synchronized has the same guarantee.
    private static void demonstrateReentrancy() {
        System.out.println("--- reentrancy: the same thread can re-lock ---");
        ReentrantLock lock = new ReentrantLock();

        lock.lock();
        try {
            lock.lock(); // re-entering from the same thread - does not block
            try {
                System.out.println("re-entered successfully, hold count: " + lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}
