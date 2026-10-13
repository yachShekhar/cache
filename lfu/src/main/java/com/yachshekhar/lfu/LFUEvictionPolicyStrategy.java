package com.yachshekhar.lfu;

import com.yachsheEhar.lfu.FrequencyBasedLinkedHashSet;
import com.yachshekhar.lru.CustomLinkedHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LFUEvictionPolicyStrategy<K> implements EvictionPolicyStrategy<K> {
    private final CustomLinkedHashSet<K> frequencyBasedLinkedHashSet;

    LFUEvictionPolicyStrategy() {
        frequencyBasedLinkedHashSet = new FrequencyBasedLinkedHashSet<K>();
    }

    @Override
    public void onAccess(K key) {
        Objects.requireNonNull(key, "`Key` must not be null");
        frequencyBasedLinkedHashSet.remove(key);
        frequencyBasedLinkedHashSet.add(key);
    }

    @Override
    public void onAdd(K key) {
        Objects.requireNonNull(key, "`Key` must not be null");
        frequencyBasedLinkedHashSet.add(key);
    }

    @Override
    public void onDelete(K key) {
        Objects.requireNonNull(key, "`Key` must not be null");
        frequencyBasedLinkedHashSet.remove(key);
    }

    @Override
    public Optional<K> getEvictKey() {
        return frequencyBasedLinkedHashSet.getFirst();
    }
}
