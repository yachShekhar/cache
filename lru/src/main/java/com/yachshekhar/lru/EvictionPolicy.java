package com.yachshekhar.lru;

import java.util.Optional;

public interface EvictionPolicy<K> {
    void onAccess(K key);
    void onAdd(K key);
    void onDelete(K key);
    Optional<K> getEvictKey();
}
