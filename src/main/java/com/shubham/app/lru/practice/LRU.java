package com.shubham.app.lru.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LRU<K, V> {

    private int capacity;
    private Node<K, V> head = new Node<>(null, null);
    private Node<K, V> tail = new Node<>(null, null);
    private Map<K, Node<K, V>> keyToNode;


    public LRU(int capacity) {
        this.capacity = capacity;
        this.keyToNode = new HashMap<>();
        head.next = tail;
        tail.prev = head;

    }

    public Optional<V> get(K key){
        return Optional.empty();
    }

    public void put(K key, V value){

    }


    private void remove(Node<K, V> node){
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addAtStart(Node<K, V> node){
        node.next = head.next;
        node.prev = head;
        head.next = node;
        node.next.prev = node;
    }

    private void moveToFront(Node<K, V> node){
        remove(node);
        addAtStart(node);
    }


    private class Node<K, V> {

        private K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;

        public Node(V value, K key) {
            this.value = value;
            this.key = key;
        }
    }

}


