package com.yachshekhar.lru;

import java.util.Optional;

public interface CustomLinkedHashSet<E> {
    public boolean add(E e);
    public boolean remove(E e);
    public Optional<E> getFirst();
}

