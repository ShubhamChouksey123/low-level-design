package com.shubham.app.lru.ideal.service;

import com.shubham.app.lru.ideal.entity.EvictionListener;

public class LoggingEvictionListener<K, V> implements EvictionListener<K, V> {

    @Override
    public void onEvict(K key, V value) {
        System.out.println("Evicted: " + key + " -> " + value);
    }
}
