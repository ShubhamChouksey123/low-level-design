package com.shubham.app.lru.practice.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Lru<K, V> {

    private int capacity;
    private final Node<K, V> first;
    private final Node<K, V> last;
    private Map<K, Node<K, V>> keyToNode;

    public Lru(int capacity) {
        this.capacity = capacity;
        this.first = new Node<>(null, null);
        this.last = new Node<>(null, null);
        this.keyToNode = new HashMap<>();

        first.setNext(last);
        last.setPrev(first);
    }

    public void add(K key, V value) {

        Node<K, V> node = keyToNode.get(key);
        if (node == null) {
            node = new Node<>(key, value);
            addNodeAtStart(node);
            keyToNode.put(key, node);
            checkCapacityAndRemoveLasNode();
            return;
        }
        node.setValue(value);
        removeNode(node);
        addNodeAtStart(node);
    }

    public Optional<V> get(K key) {

        Node<K, V> node = keyToNode.get(key);
        if (node == null) {
            return Optional.empty();
        }

        removeNode(node);
        addNodeAtStart(node);
        return Optional.of(node.getValue());
    }

    private void checkCapacityAndRemoveLasNode() {
        if (keyToNode.size() > capacity) {
            removeLastNode();
        }
    }

    private void removeNode(Node<K, V> node) {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    private void removeLastNode() {
        Node<K, V> lastNode = last.getPrev();
        removeNode(lastNode);
        keyToNode.remove(lastNode.getKey());
    }

    private void addNodeAtStart(Node<K, V> node) {
        node.setNext(first.getNext());
        node.setPrev(first);

        node.getNext().setPrev(node);
        first.setNext(node);
    }
}
