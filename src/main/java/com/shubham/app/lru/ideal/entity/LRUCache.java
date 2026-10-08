package com.shubham.app.lru.ideal.entity;

import com.shubham.app.lru.ideal.exception.InvalidCapacityException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LRUCache<K, V> {

    // Package-private-in-spirit: this is pure bookkeeping for the doubly linked
    // list, with no
    // identity or behavior of its own worth exposing — so it's a private nested
    // class instead
    // of its own file, unlike every other resource entity in this repo.
    private static class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> keyToNode = new HashMap<>();
    private final EvictionListener<K, V> evictionListener;

    // Sentinel head/tail nodes (never hold a real key/value) so every real node
    // always has a
    // real prev and next — no null checks at the ends of the list.
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    public LRUCache(int capacity, EvictionListener<K, V> evictionListener) {
        if (capacity <= 0) {
            throw new InvalidCapacityException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        this.evictionListener = evictionListener;
        head.next = tail;
        tail.prev = head;
    }

    // A cache miss is the normal case for a cache, not an error — Optional.empty(),
    // never an
    // exception.
    public synchronized Optional<V> get(K key) {
        Node<K, V> node = keyToNode.get(key);
        if (node == null) {
            return Optional.empty();
        }

        moveToFront(node);
        return Optional.of(node.value);
    }

    public synchronized void put(K key, V value) {
        Node<K, V> existing = keyToNode.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        if (keyToNode.size() == capacity) {
            evictLeastRecentlyUsed();
        }

        Node<K, V> node = new Node<>(key, value);
        keyToNode.put(key, node);
        addToFront(node);
    }

    private void evictLeastRecentlyUsed() {
        Node<K, V> leastRecentlyUsed = tail.prev;
        remove(leastRecentlyUsed);
        keyToNode.remove(leastRecentlyUsed.key);

        if (evictionListener != null) {
            evictionListener.onEvict(leastRecentlyUsed.key, leastRecentlyUsed.value);
        }
    }

    private void moveToFront(Node<K, V> node) {
        remove(node);
        addToFront(node);
    }

    private void addToFront(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
