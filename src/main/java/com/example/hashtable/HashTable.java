package com.example.hashtable;

import java.util.List;

/**
 * Provides list-based implementation of hash table with String as both key and value type.
 */
public class HashTable {

    static final private int CAPACITY_MULTIPLIER = 2;
    static final private int INVERSE_LOAD_FACTOR = 2;
    static final private int INIT_CAPACITY = 11;

    private ListMap[] buckets;
    private int entriesCount;

    /**
     * Constructs an empty HashTable
     */
    public HashTable() {
        clear();
    }

    /**
     * Checks if the specified key is present among stored keys.
     * @return     <code>true</code> if this HashTable contains such key;
     *             <code>false</code> otherwise.
     */
    public boolean contains(String key) {
        return getBucket(key).contains(key);
    }

    /**
     * Returns value mapped to the specified key or null if this HashTable contains no mapping for the key.
     * @return  the value, mapped to the key if it is present;
     * 			<code>null</code> otherwise
     */
    public String get(String key) {
        return getBucket(key).get(key);
    }

    /**
     * Stores value by key. Rewrites existing value.
     * @return  the previous value stored by this key,
     * 			or <code>null</code> if there were no previous value
     */
    public String put(String key, String value) {
        ListMap l = getBucket(key);
        int oldLSize = l.size();
        String rewritten = l.put(key, value);

        entriesCount += l.size() - oldLSize;

        tryGrow();
        return rewritten;
    }


    /**
     * Removes value stored by specified key
     * @return 	the value stored by this key,
     * 			or <code>null</code> if there were no previous value
     */
    public String remove(String key) {
        ListMap l = getBucket(key);
        int oldLSize = l.size();
        String removed = l.remove(key);
        
        entriesCount += l.size() - oldLSize;

        return removed;
    }

    /**
     * Removes all entries from HashTable
     */
    public void clear() {
        buckets = new ListMap[INIT_CAPACITY];
        for (int i = 0; i < buckets.length; i++)
            buckets[i] = new ListMap();
        entriesCount = 0;
    }


    /**
     * Returns the number of mappings stored.
     * @return	the number of mapping stored
     */
    public int size() {
        return entriesCount;
    }

    private ListMap getBucket(String key) {
        return buckets[Math.floorMod(extendedHashCode(key), buckets.length)];
    }

    private void tryGrow() {
        if (size() * INVERSE_LOAD_FACTOR < buckets.length) {
            return;
        }

        int capacityNew = buckets.length * CAPACITY_MULTIPLIER;
        var bucketsNew = new ListMap[capacityNew];

        for (int i = 0; i < capacityNew; i++) {
            bucketsNew[i] = new ListMap();
        }
        for (var l : buckets) {
            for (var entry = l.pop(); entry != null; entry = l.pop()) {
                bucketsNew[Math.floorMod(extendedHashCode(entry.key), capacityNew)].put(entry.key, entry.value);
            }
        }

        buckets = bucketsNew;
    }

    private int extendedHashCode(String s) {
        if (s == null) {
            return 0;
        } else {
            return s.hashCode();
        }
    }
}
