package com.shubham.app.hashmapimp;

import com.shubham.app.hashmapimp.dto.Node;

import java.util.Objects;

public class CustomHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;

    private int capacity;
    private Node[] nodes;

    public CustomHashMap(Integer capacity) {

        this.capacity = DEFAULT_CAPACITY;
        if (capacity != null) {
            this.capacity = capacity;
        }
        this.nodes = new Node[this.capacity];
    }

    public void put(K key, V value) {
        int hash = key.hashCode() % capacity;

        if (nodes[hash] == null) {
            nodes[hash] = new Node<>(key, value);
            return;
        }

        for (Node node : nodes) {
            if (node != null && Objects.equals(node.getKey(), key)) {
                node.setValue(value);
                return;
            }
        }

        Node currentNode = nodes[hash];
        while (currentNode.getNext() != null) {
            currentNode = currentNode.getNext();
        }

        currentNode.setNext(new Node<>(key, value));
    }

    public V get(K key) {

        int hash = key.hashCode() % capacity;

        if (nodes[hash] == null) {
            return null;
        }

        for (Node node : nodes) {
            if (node != null && Objects.equals(node.getKey(), key)) {
                return (V) node.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String str = "";

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                str = str + " for i : " + i + " => { " + nodes[i].toString() + " }\n";
            } else {
                str = str + " for i : " + i + " => { " + " }\n";
            }
        }

        // for (Node node : nodes) {
        // if (node != null) {
        // str = str + node.toString();
        // }
        // }
        return str;
    }
}
