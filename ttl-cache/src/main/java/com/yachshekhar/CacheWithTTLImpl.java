package com.yachshekhar;

import com.yachshekhar.lru.EvictionPolicy;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

interface Clock {
    long nowMillis();
}

class SystemClock implements Clock {
    @Override
    public long nowMillis() {
        return Instant.now().toEpochMilli();
    }
}

class FakeClock implements Clock {
    private Instant instant;
    FakeClock(Instant instant) {
        this.instant = instant;
    }
    @Override
    public long nowMillis() {
        return instant.toEpochMilli();
    }

    public void advance(Duration duration){
        instant = instant.plusMillis(duration.toMillis());
    }
}
public class CacheWithTTLImpl<K, V> implements CacheWithTTL<K, V>{
    private final long capacity;
    private final Map<K, StorageEntry<K, V>> storage;
    private final EvictionPolicy<K> evictionStrategy;
    private final Clock clock;
    private final TreeSet<ExpiryRef<K>> expiryIndex = new TreeSet<>(Comparator.comparingLong((ExpiryRef<K> r) -> r.expiresAtMillis()).thenComparing(ExpiryRef::seq));
    private long seqCounter = 0;
    record ExpiryRef<K>(K key, long expiresAtMillis, long seq){ }
    record StorageEntry<K, V>(V Value, ExpiryRef<K> expiryRef){
        boolean isExpired(long nowInMills) {
            return expiryRef!= null && nowInMills >= expiryRef.expiresAtMillis;
        }
    }
    public CacheWithTTLImpl(long capacity, EvictionPolicy<K> evictionStrategy, Clock clock){
        if (capacity <= 0) throw new IllegalStateException("Capacity must not less than or equals to zero");
        this.capacity=capacity;
        this.evictionStrategy = Objects.requireNonNull(evictionStrategy, "evictionStrategy must not be null");
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
        this.storage = new HashMap<>();
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(ttl, "key must not be null");
        if(ttl.isNegative()) throw new IllegalStateException("ttl must not be negative value");
        ExpiryRef<K> expiryRef = ttl == Duration.ZERO ? null: new ExpiryRef<>(key, clock.nowMillis() + ttl.toMillis(), seqCounter++);
        StorageEntry<K,V> newStorageEntry = new StorageEntry<>(value, expiryRef);
        StorageEntry<K, V> oldStorageEntry = storage.get(key);
        if (oldStorageEntry != null) {
            ExpiryRef<K> oldExpiryRef = oldStorageEntry.expiryRef();
            expiryIndex.remove(oldExpiryRef);
            storage.put(key, newStorageEntry);
            evictionStrategy.onAccess(key);
            return;
        }
        if (this.capacity == storage.size()){
            makeRoom();
        }

        storage.put(key, newStorageEntry);
        evictionStrategy.onAdd(key);
    }
    private void makeRoom() {
        if(deletedExpired()){
            return;
        }
        K evictKey = evictionStrategy.getEvictKey().orElseThrow(() -> new IllegalStateException("Capacity is full but evictKey not found"));
        StorageEntry<K, V> storageEntry = storage.remove(evictKey);
        if(storageEntry.expiryRef() != null) expiryIndex.remove(storageEntry.expiryRef());
        evictionStrategy.onDelete(evictKey);
    }
    private boolean deletedExpired() {
        if (!expiryIndex.isEmpty() && expiryIndex.first().expiresAtMillis() <= clock.nowMillis()) {
            ExpiryRef<K> expiryRef = expiryIndex.pollFirst();
            K evictKey = expiryRef.key();
            StorageEntry<K, V> storageEntry = storage.remove(evictKey);
            if (storageEntry != null){
                evictionStrategy.onDelete(evictKey);
            }
            return  true;
        }
        return false;
    }

    @Override
    public void put(K key, V value) {
        put(key, value, Duration.ZERO);
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Key must not be null");
        StorageEntry<K, V> oldStorageEntry = storage.get(key);
        if (oldStorageEntry == null) {
            return Optional.empty();
        }
        if(oldStorageEntry.isExpired(clock.nowMillis())) {
            expiryIndex.remove(oldStorageEntry.expiryRef());
            storage.remove(key);
            evictionStrategy.onDelete(key);
            return Optional.empty();
        }
        evictionStrategy.onAccess(key);
        return Optional.of(storage.get(key).Value());
    }

    @Override
    public void delete(K key) {
        Objects.requireNonNull(key, "Key must not be null");
        StorageEntry<K, V> storageEntry = storage.remove(key);
        if (storageEntry != null){
            expiryIndex.remove(storageEntry.expiryRef());
            evictionStrategy.onDelete(key);
        }
    }
}
