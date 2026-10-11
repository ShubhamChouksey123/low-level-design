package com.shubham.app.concurrencyprimitives.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// Same shape as the withdraw example in concepts/concurrency-primitives.md,
// extended with tryLock variants so Main.java can demonstrate each one.
public class BankAccount {

    private final ReentrantLock lock = new ReentrantLock();
    private double balance;

    public BankAccount(double openingBalance) {
        this.balance = openingBalance;
    }

    public double getBalance() {
        return balance;
    }

    // Blocks until the lock is free, same as a synchronized method would.
    public boolean withdraw(double amount) {
        lock.lock();
        try {
            if (amount > balance) {
                return false;
            }
            balance -= amount;
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Non-blocking: bails out immediately instead of waiting, which
    // synchronized has no way to express.
    public boolean tryWithdrawWithoutWaiting(double amount) {
        if (!lock.tryLock()) {
            return false;
        }
        try {
            if (amount > balance) {
                return false;
            }
            balance -= amount;
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Waits up to timeoutMillis for the lock before giving up, instead of
    // blocking forever or not waiting at all.
    public boolean tryWithdrawWithTimeout(double amount, long timeoutMillis) throws InterruptedException {
        if (!lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
            return false;
        }
        try {
            if (amount > balance) {
                return false;
            }
            balance -= amount;
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Holds the lock for holdMillis so Main.java can demonstrate tryLock()
    // failing/waiting from a second thread while this one is still inside.
    public void holdLockFor(long holdMillis) throws InterruptedException {
        lock.lock();
        try {
            Thread.sleep(holdMillis);
        } finally {
            lock.unlock();
        }
    }
}
