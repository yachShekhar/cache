package com.yachshekhar.lru;

import java.util.Objects;

public class Node<K> {
    private Node<K> prev;
    private Node<K> next;
    private K key;

    public Node<K> getPrev() {
        return prev;
    }

    public void setPrev(Node<K> prev) {
        this.prev = prev;
    }

    public Node<K> getNext() {
        return next;
    }

    public void setNext(Node<K> next) {
        this.next = next;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = Objects.requireNonNull(key, "``Key must not be null");
    }
}
