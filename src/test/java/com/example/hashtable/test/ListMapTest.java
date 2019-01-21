package com.example.hashtable.test;

import com.example.hashtable.ListMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListMapTest {
    @Test
    void containsBasic() {
        ListMap m1 = new ListMap();
        ListMap m2 = new ListMap();

        m1.put("looooooooong string", "some string");
        m1.put("bob", "");
        m1.put("", "extra loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong string");

        assertTrue(m1.contains("looooooooong string"));
        assertTrue(m1.contains("bob"));
        assertTrue(m1.contains(""));
        assertFalse(m1.contains("d"));
        assertFalse(m1.contains("kek"));

        m2.put("aaa", "2");
        m2.put("bob", "3");

        assertFalse(m2.contains("a"));
        assertFalse(m2.contains(""));
    }

    @Test
    void containsNullTrue() {
        ListMap m = new ListMap();

        m.put(null, "a");
        assertTrue(m.contains(null));
    }

    @Test
    void containsNullFalse() {
        ListMap m = new ListMap();

        m.put("not null", "a");
        assertFalse(m.contains(null));
    }

    @Test
    void containswithManyKeys() {
        ListMap m = new ListMap();

        m.put("a", "aa");
        m.put("b", "bb");
        m.put("c", "cc");

        addTenKeys(m);

        assertTrue(m.contains("a"));
        assertTrue(m.contains("b"));
        assertTrue(m.contains("c"));
        assertFalse(m.contains("d"));
        assertFalse(m.contains("e"));
    }

    @Test
    void containsChecksKeysNotValues() {
        ListMap m = new ListMap();

        m.put("", "a");
        assertTrue(m.contains(""));
        assertFalse(m.contains("a"));
    }

    @Test
    void getBasic() {
        ListMap m = new ListMap();

        m.put("aaa", "a");
        m.put("bbb", "b");

        assertEquals("a", m.get("aaa"));
        assertEquals("b", m.get("bbb"));
        assertNull(m.get("ccc"));
    }

    @Test
    void getWithManyKeys() {
        ListMap m = new ListMap();

        m.put("aaa", "a");
        m.put("bbb", "b");

        addTenKeys(m);

        assertEquals("a", m.get("aaa"));
        assertEquals("b", m.get("bbb"));
        assertNull(m.get("ccc"));
    }

    @Test
    void getCollisionWithManyKeys() {
// two strings with the same hasCode
        String a = "FB";
        String b = "Ea";

        if (a.hashCode() != b.hashCode()) {
            fail("Bad test");
        }

        ListMap m = new ListMap();
        m.put(a, "a");
        m.put(b, "b");

        addTenKeys(m);

        assertEquals("a", m.get(new String(a)));
        assertEquals("b", m.get(new String(b)));

        ListMap m2 = new ListMap();
        m2.put(a, "a");
        assertNull(m2.get(b));
    }

    @Test
    void removeNull() {
        ListMap m = new ListMap();

        m.put(null, "a");
        assertEquals("a", m.remove(null));
    }

    @Test
    void putMultipleValuesWithSameKey() {
        ListMap m = new ListMap();

        m.put("a", "1");
        assertEquals("1", m.put("a", "2"));

        assertEquals("2", m.get("a"));
    }

    @Test
    void removeBasic() {
        ListMap m = new ListMap();

        m.put("a", "aa");
        m.put("b", "bb");

        assertEquals("bb", m.remove("b"));

        assertNull(m.get("b"));
        assertEquals("aa", m.get("a"));
    }

    @Test
    void removeMultipleValuesWithSameKey() {
        ListMap m = new ListMap();

        m.put("b", "bb");
        m.put("b", "cc");

        m.remove("b");

        assertNull(m.get("b"));
    }

    @Test
    void clear() {
        ListMap m = new ListMap();

        m.put("a", "aa");
        m.put("b", "bb");

        m.clear();

        assertEquals(0, m.size());
        assertNull(m.get("a"));
        assertNull(m.get("b"));
    }

    @Test
    void size() {
        ListMap m = new ListMap();
        for (int i = 0; i < 100; i++) {
            assertEquals(i, m.size());
            m.put(String.valueOf(i), "");
        }
    }

    @Test
    void sizeAfterOperationsWithNull() {
        ListMap m = new ListMap();
        m.put(null, null);
        assertEquals(1, m.size());
        m.put(null, "a");
        assertEquals(1, m.size());
        m.put("a", null);
        assertEquals(2, m.size());
        m.put("a", "a");
        assertEquals(2, m.size());
    }

    @Test
    void popEmpty() {
        ListMap m = new ListMap();

        assertNull(m.pop());
    }

    @Test
    void popReturn() {
        ListMap m = new ListMap();
        m.put("a", "aa");

        ListMap.Entry e = m.pop();
        assertEquals("a", e.key);
        assertEquals("aa", e.value);
    }

    @Test
    void popErases() {
        ListMap m = new ListMap();
        m.put("a", "aa");

        ListMap.Entry e = m.pop();
        assertEquals(0, m.size());
        assertNull(m.pop());
    }

    private static void addTenKeys(ListMap m) {
        for (int i = 0; i < 10; i++) {
            m.put(String.valueOf(i), "string");
        }
    }
}