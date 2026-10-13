package com.yachshekhar;

import com.yachshekhar.lru.LRUEvictionPolicy;

import java.time.Duration;
import java.time.Instant;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Duration neverExpires = Duration.ofDays(1_000);
        FakeClock clock = new FakeClock(Instant.ofEpochMilli(1_000)); // Start clock at 1 sec from Epoch
        CacheWithTTL<Integer, String> lruCache = new CacheWithTTLImpl<>(2, new LRUEvictionPolicy<>(), clock);
        // When no expiry is set: the lru one is removed
        lruCache.put(1, "one");
        lruCache.put(2, "two");
        lruCache.put(3, "three"); // should remove 1
        IO.println(lruCache.get(1).orElse("<miss>"));
        // test that when the expiry is set the one with the expiry is removed first and not lru
        lruCache = new CacheWithTTLImpl<>(2, new LRUEvictionPolicy<>(), clock);
        lruCache.put(1, "one");
        lruCache.put(2, "two", Duration.ofSeconds(1));
        clock.advance(Duration.ofSeconds(2));
        lruCache.put(3, "three"); // should remove 2
        IO.println(lruCache.get(2).orElse("<miss>"));
    }
}
