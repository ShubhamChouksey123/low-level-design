package com.shubham.app.lru.practice.entity;

public class Node<K, V> {

    private K key;
    private V value;
    private Node<K, V> prev;
    private Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.prev = null;
        this.next = null;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Node<K, V> getPrev() {
        return prev;
    }

    public Node<K, V> getNext() {
        return next;
    }

    public void setPrev(Node<K, V> prev) {
        this.prev = prev;
    }

    public void setNext(Node<K, V> next) {
        this.next = next;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
