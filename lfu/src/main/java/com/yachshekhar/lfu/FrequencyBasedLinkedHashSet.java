package com.yachsheEhar.lfu;


import com.yachshekhar.lru.CustomLinkedHashSet;
import com.yachshekhar.lru.CustomLinkedHashSetImpl;

import java.util.*;

public class FrequencyBasedLinkedHashSet<E> implements CustomLinkedHashSet<E> {
    private final Node<E> head, tail;
    private final Map<E, Node<E>> map;

    public FrequencyBasedLinkedHashSet() {
        this.map = new HashMap<>();
        head = new Node<>(0);
        tail = new Node<>(Long.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    private static class Node<E> {
        long frequency;
        CustomLinkedHashSet<E> lru;
        Node<E> prev;
        Node<E> next;
        Node(long frequency) {
            this.frequency = frequency;
            lru = new CustomLinkedHashSetImpl<>();
        }
    }

    @Override
    public boolean add(E e) {
        if (map.containsKey(e)) return false;
        //map empty || head.next.frequency != 1 ==> create new node with frequency 1
        var node = map.isEmpty() || head.next.frequency != 1 ? insertAfter(head, new Node<E>(1)) : head.next;
        map.put(e, node);
        node.lru.add(e);
        return true;
    }

    private Node<E> insertAfter(Node<E> target, Node<E> newNode) {
        newNode.next = target.next;
        newNode.prev = target;
        target.next.prev = newNode;
        target.next = newNode;
        return newNode;
    }

    @Override
    public boolean remove(E e) {
        Node<E> node = map.get(e);
        if (node == null) return false;
        node.lru.remove(e);
        map.remove(e);
        return false;
    }

    @Override
    public Optional<E> getFirst() {
        if (map.isEmpty()) return Optional.empty();
        return head.next.lru.getFirst();
    }
}
