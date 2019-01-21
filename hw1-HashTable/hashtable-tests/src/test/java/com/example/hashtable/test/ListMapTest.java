package com.example.hashtable.test;

import com.example.hashtable.ListMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListMapTest {
    static private ListMap map;

    @BeforeEach
    void initM() {
        map = new ListMap();
    }

    @Test
    void containsBasic() {
        var map1 = new ListMap();
        var map2 = new ListMap();

        map1.put("looooooooong string", "some string");
        map1.put("bob", "");
        map1.put("", "extra loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong string");

        assertTrue(map1.contains("looooooooong string"));
        assertTrue(map1.contains("bob"));
        assertTrue(map1.contains(""));
        assertFalse(map1.contains("d"));
        assertFalse(map1.contains("kek"));

        map2.put("aaa", "2");
        map2.put("bob", "3");

        assertFalse(map2.contains("a"));
        assertFalse(map2.contains(""));
    }

    @Test
    void containsNullTrue() {
        map.put(null, "a");
        assertTrue(map.contains(null));
    }

    @Test
    void containsNullFalse() {
        map.put("not null", "a");
        assertFalse(map.contains(null));
    }

    @Test
    void containswithManyKeys() {
        map.put("a", "aa");
        map.put("b", "bb");
        map.put("c", "cc");

        addTenKeys(map);

        assertTrue(map.contains("a"));
        assertTrue(map.contains("b"));
        assertTrue(map.contains("c"));
        assertFalse(map.contains("d"));
        assertFalse(map.contains("e"));
    }

    @Test
    void containsChecksKeysNotValues() {
        map.put("", "a");
        assertTrue(map.contains(""));
        assertFalse(map.contains("a"));
    }

    @Test
    void getBasic() {
        map.put("aaa", "a");
        map.put("bbb", "b");

        assertEquals("a", map.get("aaa"));
        assertEquals("b", map.get("bbb"));
        assertNull(map.get("ccc"));
    }

    @Test
    void getWithManyKeys() {
        map.put("aaa", "a");
        map.put("bbb", "b");

        addTenKeys(map);

        assertEquals("a", map.get("aaa"));
        assertEquals("b", map.get("bbb"));
        assertNull(map.get("ccc"));
    }

    @Test
    void removeNull() {
        map.put(null, "a");
        assertEquals("a", map.remove(null));
    }

    @Test
    void putMultipleValuesWithSameKey() {
        map.put("a", "1");
        assertEquals("1", map.put("a", "2"));

        assertEquals("2", map.get("a"));
    }

    @Test
    void removeBasic() {
        map.put("a", "aa");
        map.put("b", "bb");

        assertEquals("bb", map.remove("b"));

        assertNull(map.get("b"));
        assertEquals("aa", map.get("a"));
    }

    @Test
    void removeMultipleValuesWithSameKey() {
        map.put("b", "bb");
        map.put("b", "cc");

        map.remove("b");

        assertNull(map.get("b"));
    }

    @Test
    void clear() {
        map.put("a", "aa");
        map.put("b", "bb");

        map.clear();

        assertEquals(0, map.size());
        assertNull(map.get("a"));
        assertNull(map.get("b"));
    }

    @Test
    void size() {
        for (int i = 0; i < 100; i++) {
            assertEquals(i, map.size());
            map.put(String.valueOf(i), "");
        }
    }

    @Test
    void sizeAfterOperationsWithNull() {
        map.put(null, null);
        assertEquals(1, map.size());
        map.put(null, "a");
        assertEquals(1, map.size());
        map.put("a", null);
        assertEquals(2, map.size());
        map.put("a", "a");
        assertEquals(2, map.size());
    }

    @Test
    void popEmpty() {
        assertNull(map.pop());
    }

    @Test
    void popReturn() {
        map.put("a", "aa");

        ListMap.Entry e = map.pop();
        assertEquals("a", e.key);
        assertEquals("aa", e.value);
    }

    @Test
    void popErases() {
        map.put("a", "aa");

        ListMap.Entry e = map.pop();
        assertEquals(0, map.size());
        assertNull(map.pop());
    }

    private static void addTenKeys(ListMap m) {
        for (int i = 0; i < 10; i++) {
            m.put(String.valueOf(i), "string");
        }
    }
}