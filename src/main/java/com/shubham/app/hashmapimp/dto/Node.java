package com.shubham.app.hashmapimp.dto;

public class Node<K, V> {

    private K key;
    private V value;
    private Node<K, V> next;

    public Node(K key, V value, Node<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node<K, V> getNext() {
        return next;
    }

    public void setNext(Node<K, V> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return toString1();
    }

    public String toString1() {
        String str = "";
        Node<K, V> current = this;
        while (current != null) {
            str = str + "Node{" + "key=" + current.getKey() + ", value=" + current.getValue() + '}' + " ";

            current = current.getNext();
        }

        return str;
    }
}
