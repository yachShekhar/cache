package com.yachshekhar.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomLinkedHashSetImpl<E> implements  CustomLinkedHashSet<E> {
    private static class Node<E> {
        E element;
        Node<E> prev;
        Node<E> next;
        // Constructor for real data nodes
        Node(E element) { this.element = element; }

        // Constructor for Sentinel nodes
        Node() { this.element = null; }
    }
    private final Map<E, Node<E>> map;

    private final Node<E> head, tail;
    public CustomLinkedHashSetImpl() {
        map = new HashMap<>();
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public boolean add(E e) {
        if (map.containsKey(e)) return false;
        var node = new Node<E>(e);
        insertBefore(tail, node);
        map.put(e, node);
        return true;
    }

    private void insertBefore(Node<E> targetNode, Node<E> newNode){
        newNode.next = targetNode;
        newNode.prev = targetNode.prev;
        targetNode.prev.next = newNode;
        targetNode.prev = newNode;
    }

    private void  removeNode(Node<E> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    @Override
    public boolean remove(E e) {
        Node<E>  nodeElement =  map.remove(e);
        if (nodeElement == null) return false;
        removeNode(nodeElement);
        return true;
    }

    @Override
    public Optional<E> getFirst() {
        if (map.isEmpty()) return Optional.empty();
        return Optional.of(head.next.element);
    }
}
