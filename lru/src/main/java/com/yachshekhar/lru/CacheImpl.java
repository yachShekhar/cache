package com.yachshekhar.lru;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CacheImpl<K, V> implements Cache<K, V> {
    private final Map<K,V> storage;
    private final EvictionPolicy<K> evictionPolicy;
    private final long capacity;
    private final ReentrantLock lock;
    public CacheImpl(long capacity, LRUEvictionPolicy<K> evictionPolicy) {
        if(capacity <=0) throw new IllegalStateException("`Capacity` should be greater than zero");
        this.capacity = capacity;
        this.evictionPolicy = Objects.requireNonNull(evictionPolicy, "`evictionStrategy` can not be null");
        storage = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }
     @Override
    public void put(K key, V value) {
         Objects.requireNonNull(key, "Key must not be null");
         Objects.requireNonNull(value, "Value must not be null");
         lock.lock();
         try {
             // Overriding a key -> no eviction happens here
             if (storage.containsKey(key)) {
                 storage.put(key, value);
                 evictionPolicy.onAccess(key);
                 return;
             }
             if (storage.size() == capacity) {
                 K evictKey = evictionPolicy.getEvictKey()
                         .orElseThrow(()->new IllegalStateException("Cache is full but eviction policy has no key to evict."));
                 delete(evictKey);
             }
             storage.put(key, value);
             evictionPolicy.onAdd(key);
         } finally {
             lock.unlock();
         }
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "`Key` can not be null");
        lock.lock();
        try {
            if (!storage.containsKey(key)){
                return Optional.empty();
            }
            evictionPolicy.onAccess(key);
            return Optional.of(storage.get(key));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        Objects.requireNonNull(key, "`Key` can not be null");
        lock.lock();
        try {
            storage.remove(key);
            evictionPolicy.onDelete(key);
        } finally {
            lock.unlock();
        }

    }
}
