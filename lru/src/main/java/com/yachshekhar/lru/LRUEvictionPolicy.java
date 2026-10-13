package com.yachshekhar.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LRUEvictionPolicy<K> implements  EvictionPolicy<K> {

    private final CustomLinkedHashSet<K> linkedHashSet;
    public LRUEvictionPolicy() {
        linkedHashSet= new CustomLinkedHashSetImpl<K>();
    }
    @Override
    public void onAccess(K key) {
        Objects.requireNonNull(key, "`key` must not be null");
        linkedHashSet.remove(key);
        linkedHashSet.add(key);
    }
    @Override
    public void onAdd(K key) {
        Objects.requireNonNull(key, "`key` must not be null");
        linkedHashSet.add(key);
    }

    @Override
    public void onDelete(K key) {
        Objects.requireNonNull(key, "`key` must not be null");
        linkedHashSet.remove(key);
    }

    @Override
    public Optional<K> getEvictKey() {
        return linkedHashSet.getFirst();
    }
}
