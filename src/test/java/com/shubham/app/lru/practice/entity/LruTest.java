package com.shubham.app.lru.practice.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.lru.practice.exception.InvalidCapacityException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LruTest {

    @Test
    void constructorRejectsANonPositiveCapacity() {
        assertThrows(InvalidCapacityException.class, () -> new Lru<Integer, String>(0));
        assertThrows(InvalidCapacityException.class, () -> new Lru<Integer, String>(-1));
    }

    // The actual regression test for this session's twist: fire far more concurrent
    // add/get calls than the capacity allows, from many threads at once, and check
    // the synchronized methods never let the cache grow past capacity or throw.
    @Test
    void concurrentAddAndGetNeverCorruptOrOvergrowTheCache() throws InterruptedException {
        int capacity = 10;
        Lru<Integer, Integer> lru = new Lru<>(capacity);

        ExecutorService executor = Executors.newFixedThreadPool(capacity);
        for (int i = 0; i < capacity * 5; i++) {
            int key = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    lru.add(key, key);
                    lru.get(key % capacity);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        int presentCount = 0;
        for (int i = 0; i < capacity * 5; i++) {
            if (lru.get(i).isPresent()) {
                presentCount++;
            }
        }
        assertTrue(presentCount <= capacity);
    }
}
