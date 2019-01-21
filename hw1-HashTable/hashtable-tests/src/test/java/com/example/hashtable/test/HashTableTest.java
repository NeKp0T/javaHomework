package com.example.hashtable.test;

import com.example.hashtable.HashTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {
    private HashTable table;

    @BeforeEach
    void initM() {
        table = new HashTable();
    }

    @Test
    void containsBasic() {
        var table1 = new HashTable();
        var table2 = new HashTable();

        table1.put("looooooooong string", "some string");
        table1.put("bob", "");
        table1.put("", "extra loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong string");

        assertTrue(table1.contains("looooooooong string"));
        assertTrue(table1.contains("bob"));
        assertTrue(table1.contains(""));
        assertFalse(table1.contains("d"));
        assertFalse(table1.contains("kek"));

        table2.put("aaa", "2");
        table2.put("bob", "3");

        assertFalse(table2.contains("a"));
        assertFalse(table2.contains(""));
    }

    @Test
    void containsNullTrue() {
        table.put(null, "a");
        assertTrue(table.contains(null));
    }

    @Test
    void containsNullFalse() {
        HashTable m = new HashTable();

        m.put("not null", "a");
        assertFalse(m.contains(null));
    }

    @Test
    void containsAfterRehash() {
        table.put("a", "aa");
        table.put("b", "bb");
        table.put("c", "cc");

        causeRehash(table);

        assertTrue(table.contains("a"));
        assertTrue(table.contains("b"));
        assertTrue(table.contains("c"));
        assertFalse(table.contains("d"));
        assertFalse(table.contains("e"));
    }

    @Test
    void containsChecksKeysNotValues() {
        table.put("", "a");
        assertTrue(table.contains(""));
        assertFalse(table.contains("a"));
    }

    @Test
    void containsCollision() {
        // two strings with the same hashCode
        String a = "FB";
        String b = "Ea";

        if (a.hashCode() != b.hashCode()) {
            fail("Bad test");
        }

        table.put(a, "");
        table.put(b, "");

        assertTrue(table.contains(new String(a)));
        assertTrue(table.contains(new String(b)));
        assertTrue(table.contains(a));
        assertTrue(table.contains(b));
    }

    @Test
    void getBasic() {
        table.put("aaa", "a");
        table.put("bbb", "b");

        assertEquals("a", table.get("aaa"));
        assertEquals("b", table.get("bbb"));
        assertNull(table.get("ccc"));
    }

    @Test
    void getNull() {
        table.put(null, "a");
        assertEquals("a", table.get(null));
    }

    @Test
    void getAfterRehash() {
        table.put("aaa", "a");
        table.put("bbb", "b");

        causeRehash(table);

        assertEquals("a", table.get("aaa"));
        assertEquals("b", table.get("bbb"));
        assertNull(table.get("ccc"));
    }

    @Test void getCollision() {
        // two strings with the same hashCode
        String a = "FB";
        String b = "Ea";

        if (a.hashCode() != b.hashCode()) {
            fail("Bad test");
        }

        table.put(a, "a");
        table.put(b, "b");

        assertEquals("a", table.get(new String(a)));
        assertEquals("b", table.get(new String(b)));

        HashTable m2 = new HashTable();
        m2.put(a, "a");
        assertNull(m2.get(b));
    }

    @Test
    void getCollisionAfterRehash() {
        // two strings with the same hashCode
        String a = "FB";
        String b = "Ea";

        if (a.hashCode() != b.hashCode()) {
            fail("Bad test");
        }

        table.put(a, "a");
        table.put(b, "b");

        causeRehash(table);

        assertEquals("a", table.get(new String(a)));
        assertEquals("b", table.get(new String(b)));

        HashTable m2 = new HashTable();
        m2.put(a, "a");
        assertNull(m2.get(b));
    }

    @Test
    void putMultipleValuesWithSameKey() {
        table.put("a", "1");
        assertEquals("1", table.put("a", "2"));

        assertEquals("2", table.get("a"));
    }

    @Test
    void removeBasic() {
        table.put("a", "aa");
        table.put("b", "bb");

        assertEquals("bb", table.remove("b"));

        assertNull(table.get("b"));
        assertEquals("aa", table.get("a"));
    }


    @Test
    void removeNull() {
        table.put(null, "a");
        assertEquals("a", table.remove(null));
    }

    @Test
    void removeMultipleValuesWithSameKey() {
        table.put("b", "bb");
        table.put("b", "cc");

        table.remove("b");

        assertNull(table.get("b"));
    }

    @Test
    void clear() {
        table.put("a", "aa");
        table.put("b", "bb");

        table.clear();

        assertEquals(0, table.size());
        assertNull(table.get("a"));
        assertNull(table.get("b"));
    }

    @Test
    void size() {
        for (int i = 0; i < 100; i++) {
            assertEquals(i, table.size());
            table.put(String.valueOf(i), "");
        }
    }

    @Test
    void sizeAfterOperationsWithNull() {
        table.put(null, null);
        assertEquals(1, table.size());
        table.put(null, "a");
        assertEquals(1, table.size());
        table.put("a", null);
        assertEquals(2, table.size());
        table.put("a", "a");
        assertEquals(2, table.size());
    }

    private static void causeRehash(HashTable m) {
        for (int i = 0; i < 10; i++) {
            m.put(String.valueOf(i), "string");
        }
    }
}