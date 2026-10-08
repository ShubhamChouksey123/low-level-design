package com.shubham.app.lru.ideal.entity;

public interface EvictionListener<K, V> {

    void onEvict(K key, V value);
}
