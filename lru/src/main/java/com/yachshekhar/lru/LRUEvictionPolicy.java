package com.yachshekhar.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LRUEvictionPolicy<K> implements  EvictionPolicy<K> {
    private final DoublyLinkedList<K> dll;
    private final Map<K, Node<K>> keyNodeRef;
    public LRUEvictionPolicy() {
        this.dll = new DoublyLinkedList<>();
        this.keyNodeRef = new HashMap<>();
    }
    @Override
    public void onAccess(K key) {
        Objects.requireNonNull(key, "`key` must not be null");
        Node<K> node = keyNodeRef.get(key);
        if (node != null) {
            dll.remove(node); // remove the node
        } else {
            node = new Node<>();
            node.setKey(key);
        }
        dll.add(node);  // keep at first
        keyNodeRef.put(key, node);
    }

    @Override
    public void onDelete(K key) {
        Objects.requireNonNull(key, "`key` must not be null");
        Node<K> node = keyNodeRef.get(key);
        if (node != null) {
            dll.remove(node);
            keyNodeRef.remove(node.getKey());
        }
    }

    @Override
    public Optional<K> getEvictKey() {
        return dll.getLastKey();
    }
}
