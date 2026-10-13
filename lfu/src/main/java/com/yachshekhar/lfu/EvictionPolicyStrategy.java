package com.yachshekhar.lfu;

import java.util.Optional;

public interface EvictionPolicyStrategy<K> {
    void onAccess(K key);
    void onAdd(K key);
    void onDelete(K key);
    Optional<K> getEvictKey();
}
