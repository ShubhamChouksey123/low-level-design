package com.shubham.app.lru.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.lru.ideal.exception.InvalidCapacityException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LRUCacheTest {

    // Records every eviction so tests can assert on it, instead of a lambda.
    private static class RecordingEvictionListener<K, V> implements EvictionListener<K, V> {
        private final List<K> evictedKeys = new ArrayList<>();
        private final List<V> evictedValues = new ArrayList<>();

        @Override
        public void onEvict(K key, V value) {
            evictedKeys.add(key);
            evictedValues.add(value);
        }
    }

    @Test
    void constructorRejectsANonPositiveCapacity() {
        assertThrows(InvalidCapacityException.class, () -> new LRUCache<Integer, String>(0, null));
        assertThrows(InvalidCapacityException.class, () -> new LRUCache<Integer, String>(-1, null));
    }

    @Test
    void getOnAMissingKeyReturnsEmpty() {
        LRUCache<Integer, String> cache = new LRUCache<>(2, null);

        assertTrue(cache.get(1).isEmpty());
    }

    @Test
    void putThenGetReturnsTheValue() {
        LRUCache<Integer, String> cache = new LRUCache<>(2, null);

        cache.put(1, "a");

        assertEquals("a", cache.get(1).orElseThrow());
    }

    @Test
    void updatingAnExistingKeyOverwritesItsValueWithoutEvictingAnything() {
        RecordingEvictionListener<Integer, String> listener = new RecordingEvictionListener<>();
        LRUCache<Integer, String> cache = new LRUCache<>(1, listener);
        cache.put(1, "a");

        cache.put(1, "b");

        assertEquals("b", cache.get(1).orElseThrow());
        assertTrue(listener.evictedKeys.isEmpty());
    }

    @Test
    void exceedingCapacityEvictsTheLeastRecentlyUsedEntry() {
        LRUCache<Integer, String> cache = new LRUCache<>(2, null);
        cache.put(1, "a");
        cache.put(2, "b");

        cache.put(3, "c");

        assertTrue(cache.get(1).isEmpty());
        assertEquals("b", cache.get(2).orElseThrow());
        assertEquals("c", cache.get(3).orElseThrow());
    }

    // Regression-shaped test for the whole point of "LRU": reading an entry must
    // protect it
    // from eviction, the same way Session 05/06's price-priority and
    // connection-pool fixes
    // depended on getting an ordering rule exactly right.
    @Test
    void accessingAnEntryProtectsItFromEviction() {
        LRUCache<Integer, String> cache = new LRUCache<>(2, null);
        cache.put(1, "a");
        cache.put(2, "b");

        cache.get(1);
        cache.put(3, "c");

        assertEquals("a", cache.get(1).orElseThrow());
        assertTrue(cache.get(2).isEmpty());
        assertEquals("c", cache.get(3).orElseThrow());
    }

    @Test
    void evictionInvokesTheListenerWithTheEvictedKeyAndValue() {
        RecordingEvictionListener<Integer, String> listener = new RecordingEvictionListener<>();
        LRUCache<Integer, String> cache = new LRUCache<>(1, listener);
        cache.put(1, "a");

        cache.put(2, "b");

        assertEquals(List.of(1), listener.evictedKeys);
        assertEquals(List.of("a"), listener.evictedValues);
    }

    @Test
    void concurrentPutsNeverGrowTheCacheBeyondItsCapacity() throws InterruptedException {
        int capacity = 10;
        LRUCache<Integer, Integer> cache = new LRUCache<>(capacity, null);

        ExecutorService executor = Executors.newFixedThreadPool(capacity);
        for (int i = 0; i < capacity * 5; i++) {
            int key = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    cache.put(key, key);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        int presentCount = 0;
        for (int i = 0; i < capacity * 5; i++) {
            if (cache.get(i).isPresent()) {
                presentCount++;
            }
        }
        assertTrue(presentCount <= capacity);
    }
}
