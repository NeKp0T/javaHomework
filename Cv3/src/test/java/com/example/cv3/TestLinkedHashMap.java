package com.example.cv3;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestLinkedHashMap {

    private LinkedHashMap<String, String> map;

    @BeforeEach
    void init() {
        map = new LinkedHashMap<>();
    }

    private void putSome() {
        map.put("a", "b");
        map.put("b", "c");
        map.put("a", "d");
    }

    @Test
    void putCrashesTest() {
        putSome();
    }

    @Test
    void putResultTest() {
        assertEquals(null, map.put("a", "b"));
        assertEquals(null, map.put("b", "c"));
        assertEquals("b", map.put("a", "d"));
    }

    @Test
    void getTest() {
        putSome();
        assertEquals("d", map.get("a"));
        assertEquals("c", map.get("b"));
    }

    @Test
    void getAfterRehash() {
        map = new LinkedHashMap<>(1);
        getTest();
    }

    @Test
    void iteratorOrderTest() {
        putSome();
        Iterator<Map.Entry<String, String>> i = map.entrySet().iterator();
        assertEquals(new LinkedEntry<String, String>("a", "d", null), i.next());
        assertEquals(new LinkedEntry<String, String>("b", "c", null), i.next());
    }

    @Test
    void removeTest() {
        putSome();
        assertNull(map.remove("bob"));
        assertEquals("d", map.remove("a"));
    }

    @Test
    void orderAferRemoveTest() {
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        map.put("d", "d");
        map.put("e", "e");

        map.remove("a");
        map.remove("c");
        map.remove("e");

        Iterator<Map.Entry<String, String>> i = map.entrySet().iterator();
        assertEquals(new LinkedEntry<String, String>("b", "b", null), i.next());
        assertEquals(new LinkedEntry<String, String>("d", "d", null), i.next());
    }

    @Test
    void sizeTest() {
        assertEquals(0, map.size());
        map.put("a", "a");
        assertEquals(1, map.size());
        map.put("b", "b");
        assertEquals(2, map.size());
        map.put("b", "c");
        assertEquals(2, map.size());
        map.remove("b");
        assertEquals(1, map.size());
        map.put("b", "asd");
        assertEquals(2, map.size());
    }

    @Test
    void setContains() {
        Set<Map.Entry<String, String>> set = map.entrySet();
        map.put("a", "b");

        var entry = new MapEntry("a", "b");

        assertTrue(set.contains(entry));
        assertFalse(set.contains(new MapEntry("a", "a")));
        assertFalse(set.contains(new MapEntry("b", "b")));
        map.remove("a");
        assertFalse(set.contains(entry));
    }

    private class MapEntry implements Map.Entry<String, String> {

        String key;
        String value;

        public MapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            return null;
        }

        public boolean equals(Object o) {
            var other = (Map.Entry) o;
            return Objects.equals(key, other.getKey()) && Objects.equals(value, other.getValue());
        }
    }
}