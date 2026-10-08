package com.shubham.app.lru.ideal;

import com.shubham.app.lru.ideal.entity.LRUCache;
import com.shubham.app.lru.ideal.service.LoggingEvictionListener;

public class Main {

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(2, new LoggingEvictionListener<>());

        cache.put(1, "a");
        cache.put(2, "b");

        // Touching 1 makes 2 the least recently used entry.
        System.out.println("get(1): " + cache.get(1));

        // Capacity is full, so this evicts 2, not 1 — 1 was protected by the read
        // above.
        cache.put(3, "c");

        System.out.println("get(2), evicted: " + cache.get(2));
        System.out.println("get(1), still present: " + cache.get(1));
        System.out.println("get(3), just inserted: " + cache.get(3));
    }
}
