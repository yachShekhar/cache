package com.yachshekhar.lru;

public class Main {
    static void main() {
        Cache<String, String> lru = new CacheImpl<>(2);
        lru.put("a", "a");
        IO.println(lru.get("a")); // a
        lru.put("b", "b");
        lru.put("c", "c");
        IO.println(lru.get("a")); // null
        IO.println(lru.get("b")); // b
        IO.println(lru.get("c")); // c
        lru.delete("c");
        IO.println(lru.get("a")); // null
        IO.println(lru.get("b")); // b
        IO.println(lru.get("c")); // null

    }
}
