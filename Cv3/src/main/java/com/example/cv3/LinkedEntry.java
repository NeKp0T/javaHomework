package com.example.cv3;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class LinkedEntry<K, V> implements Map.Entry<K, V> {

    K key;
    V value;
    @Nullable
    LinkedEntry<K, V> nextEntry;
    @Nullable
    LinkedEntry<K, V> previousEntry;

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    LinkedEntry(K key, V value, LinkedEntry<K, V> lastLinkedEntry) {
        this.key = key;
        this.value = value;
        previousEntry = lastLinkedEntry;
        nextEntry = null;
        if (lastLinkedEntry != null) {
            lastLinkedEntry.nextEntry = this;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Map.Entry) {
            Map.Entry otherEntry = (Map.Entry) other;
            return (getKey() == null ?
                    otherEntry.getKey() == null : getKey().equals(otherEntry.getKey()))  &&
                    (getValue() == null ?
                    otherEntry.getValue()==null : getValue().equals(otherEntry.getValue()));
        }
        return false;
    }

    void eraseThis() {
        if (previousEntry != null) {
            previousEntry.nextEntry = nextEntry;
        }
        if (nextEntry != null) {
            nextEntry.previousEntry = previousEntry;
        }
    }
}
