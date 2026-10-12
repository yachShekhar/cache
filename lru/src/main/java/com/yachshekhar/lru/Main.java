package com.yachshekhar.lru;

public class Main {
    static void main() {
        Cache<String, String> lru = new CacheImpl<>(2, new LRUEvictionPolicy<>());
        lru.put("a", "a");
        IO.println(lru.get("a").orElse("<miss>")); // a
        lru.put("b", "b");
        lru.put("c", "c");
        IO.println(lru.get("a").orElse("<miss>")); // null
        IO.println(lru.get("b").orElse("<miss>")); // b
        IO.println(lru.get("c").orElse("<miss>")); // c
        lru.delete("c");
        IO.println(lru.get("a").orElse("<miss>")); // null
        IO.println(lru.get("b").orElse("<miss>")); // b
        IO.println(lru.get("c").orElse("<miss>")); // null

    }
}
