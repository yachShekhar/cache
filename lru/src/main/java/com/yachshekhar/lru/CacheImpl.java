package com.yachshekhar.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheImpl<K, V> implements Cache<K, V> {
    private final Map<K,V> storage;
    private final EvictionPolicy<K> evictionPolicy;
    private final long capacity;
    CacheImpl(long capacity, LRUEvictionPolicy<K> evictionPolicy) {
        if(capacity <=0) throw new IllegalStateException("`Capacity` should be greater than zero");
        this.capacity = capacity;
        this.evictionPolicy = Objects.requireNonNull(evictionPolicy, "`evictionStratgey` can not be null");
        storage = new ConcurrentHashMap<>();
    }
     @Override
    public void put(K key, V value) {
         Objects.requireNonNull(key, "`Key` can not be null");
         if (!storage.containsKey(key) && storage.size() == capacity) {
             if (evictionPolicy.getEvictKey().isPresent()) {
                 delete(evictionPolicy.getEvictKey().get());
             }
         }
         storage.put(key, value);
         evictionPolicy.onAccess(key);
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "`Key` can not be null");
        evictionPolicy.onAccess(key);
        return Optional.of(storage.get(key));
    }

    @Override
    public void delete(K key) {
        Objects.requireNonNull(key, "`Key` can not be null");
        storage.remove(key);
        evictionPolicy.onDelete(key);
    }
}
