package com.example.cv3;

import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO docs

// TODO put, remove
public class LinkedHashMap<K, V> extends AbstractMap<K, V> {

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntryHashSet();
    }

    @Override
    public V put(K key, V value) {
        assertKeyNotNull(key);

        ListBucket bucket = bucketArray[getPosition(key)];
        V result = bucket.put(key, value);
        if (result == null) {
            size++;
        }

        checkRehash();

        return result;
    }

    @Override
    public V get(Object key) {
        K castedKey = (K) key;
        ListBucket bucket = bucketArray[getPosition(castedKey)];
        ListBucket.Entry foundEntry = bucket.find(castedKey);
        if (foundEntry == null) {
            return null;
        }
        return foundEntry.getContained().value;
    }

    @Override
    public V remove(Object key) {
        K castedKey = (K) key;
        assertKeyNotNull(castedKey); // safe
        ListBucket bucket = bucketArray[getPosition(castedKey)]; // safe
        V result = bucket.deleteByKey(castedKey); // safe
        if (result != null) {
            size--;
        }
        return result;
    }

    @Override
    public int size() {
        return size;
    }

    // TODO think of size
    static final private int CAPACITY_MULTIPLIER = 2;
    static final private int INVERSE_LOAD_FACTOR = 2;
    static final private int INIT_CAPACITY = 11;

    private int size = 0;
    private LinkedEntry<K, V> firstLinkedEntry = null;
    private LinkedEntry<K, V> lastLinkedEntry = null;
    private ListBucket[] bucketArray;

    public LinkedHashMap() {
        this(INIT_CAPACITY);
    }

    public LinkedHashMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity can't be less then 1");
        }

        bucketArray = (ListBucket[]) new LinkedHashMap.ListBucket[capacity];
        for (int i = 0; i < bucketArray.length; i++) {
            bucketArray[i] = new ListBucket();
        }
    }

    private void assertKeyNotNull(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    private int getPosition(K key) {
        return key.hashCode() % bucketArray.length;
    }

    private void checkRehash() {
        if (size() * INVERSE_LOAD_FACTOR <= bucketArray.length) {
            return;
        }

        int newCapacity = bucketArray.length * CAPACITY_MULTIPLIER;

        LinkedHashMap<K, V> biggerCopy = new LinkedHashMap<>(newCapacity);
        for (LinkedEntry<K, V> i = firstLinkedEntry; i != null; i = i.nextEntry) {
            biggerCopy.put(i.key, i.value);
        }

        this.firstLinkedEntry = biggerCopy.firstLinkedEntry;
        this.lastLinkedEntry = biggerCopy.lastLinkedEntry;
        this.bucketArray = biggerCopy.bucketArray;
    }

    class ListBucket {
        private Entry first;

        ListBucket() {
            first = new Entry(null);
        }

        private Entry findPrevious(K key) {
            Entry previous = first;
            for (Entry i = first.getNext(); i != null; i = i.next) {
                if (key.equals(i.getContained().getKey())) {
                    return previous;
                }
                previous = i;
            }
            return null;
        }

        private Entry find(K key) {
            Entry previous = findPrevious(key);
            if (previous == null) {
                return null;
            }
            return previous.getNext();
        }

        V deleteByKey(K key) {
            Entry previous = findPrevious(key);
            if (previous != null) {
                LinkedEntry<K, V> deletedEnry = previous.next.getContained();
                V deleted = deletedEnry.getValue();

                deletedEnry.eraseThis();
                previous.next = previous.getNext().getNext();

                if (firstLinkedEntry == deletedEnry) {
                    firstLinkedEntry = deletedEnry.nextEntry;
                }
                if (lastLinkedEntry == deletedEnry) {
                    lastLinkedEntry = deletedEnry.previousEntry;
                }

                return deleted;
            } else {
                return null;
            }
        }

        V put(K key, V value) {
            Entry entry = find(key);
            if (entry != null) {
                V oldValue = entry.contained.value;
                entry.contained.value = value;
                return oldValue;
            } else {
                LinkedEntry<K, V> newLinkedEntry = new LinkedEntry<>(key, value, lastLinkedEntry);
                if (firstLinkedEntry == null) {
                    firstLinkedEntry = newLinkedEntry;
                }
                lastLinkedEntry = newLinkedEntry;

                Entry newEntry = new Entry(newLinkedEntry);
                newEntry.next = first.next;
                first.next = newEntry;

                return null;
            }
        }

        class Entry {
            private LinkedEntry<K, V> contained;
            private Entry next;

            public Entry getNext() {
                return next;
            }
            public LinkedEntry<K, V> getContained() {
                return contained;
            }

            public Entry(LinkedEntry<K, V> contained) {
                this.contained = contained;
            }
        }
    }

    class EntryHashSet extends AbstractSet<Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new SetIterator();
        }

        @Override
        public int size() {
            return LinkedHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) o;
            V containedValue = get(entry.getKey());
            return entry.equals(new LinkedEntry(entry.getKey(), containedValue, null));
        }

        private class SetIterator implements Iterator<Map.Entry<K, V>> {

            @Override
            public boolean hasNext() {
                return nextEntry != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (nextEntry == null) {
                    throw new NoSuchElementException();
                }
                Map.Entry<K, V> wasNext = nextEntry;
                nextEntry = nextEntry.nextEntry;
                return wasNext;
            }

            @Nullable LinkedEntry<K, V> nextEntry;

            private SetIterator() {
                nextEntry = firstLinkedEntry;
            }
        }
    }
}