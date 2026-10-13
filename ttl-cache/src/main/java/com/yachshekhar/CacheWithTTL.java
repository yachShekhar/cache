package com.yachshekhar;

import com.yachshekhar.lru.Cache;

import java.time.Duration;

public interface CacheWithTTL<K, V> extends Cache<K, V> {
    public void put(K key, V value, Duration ttl);
}
