package com.yachshekhar.lru;

import java.util.Objects;
import java.util.Optional;

public class DoublyLinkedList<K> {
    private final Node<K> head;
    private final Node<K> tail;

    DoublyLinkedList(){
        this.head = new Node<>();
        this.tail = new Node<>();
        head.setNext(tail);
        tail.setPrev(head);
    }

    void add(Node<K> node){
        Objects.requireNonNull(node, "`Node` must not be null");
        Node<K> next = head.getNext();
        head.setNext(node);
        node.setPrev(head);
        node.setNext(next);
        next.setPrev(node);
    }

    void remove(Node<K> node) {
        Objects.requireNonNull(node, "`Node` must not be null");
        Node<K> nodePrev = node.getPrev();
        Node<K> nodeNext = node.getNext();
        nodePrev.setNext(nodeNext);
        nodeNext.setPrev(nodePrev);

        //clean
        node.setPrev(null);
        node.setNext(null);
    }

    Optional<K> getLastKey(){
        if(tail.getPrev() == head) {
            return Optional.empty();
        }
        return Optional.of(tail.getPrev().getKey());
    }
}
