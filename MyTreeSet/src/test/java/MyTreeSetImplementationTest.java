import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class MyTreeSetImplementationTest {

    MyTreeSetImplementation<String> set;

    private static final String[] ELEMENTS =  new String[]{"a", "b", "c"};
    private void putSomeToSet() {
        set.add("b");
    }

    @BeforeEach
    void init() {
        set = new MyTreeSetImplementation<>();
    }

    @Test
    void addTrue() {
        assertTrue(set.add("a"));
        assertTrue(set.add("b"));
        assertTrue(set.add("c"));
    }

    @Test
    void addFalse() {
        assertTrue(set.add("a"));
        assertFalse(set.add("a"));
    }

    @Test
    void addComplicated() {
        assertTrue(set.add("a"));
        assertTrue(set.add("z"));
        assertTrue(set.add("b"));
        assertTrue(set.add("c"));
        assertFalse(set.add("b"));
        assertFalse(set.add("z"));
        assertTrue(set.add("x"));
        assertFalse(set.add("x"));
    }

    @Test
    void differentComparator() {
        assertTrue(false); // TODO
    }

    @Test
    void sizeBasic() {
        assertEquals(0, set.size());
        set.add("a");
        assertEquals(1, set.size());
    }

    @Test
    void sizeCollisions() {
        putSomeToSet();
        assertEquals(3, set.size());
    }

    @Test
    void iteratorNextAndHasNext() {
        putSomeToSet();
        var iterator = set.iterator();
        for (int i = 0; i < 3; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(ELEMENTS[i], iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingIterator() {
        putSomeToSet();
        var iterator = set.descendingIterator();
        for (int i = 2; i >= 0; i--) {
            assertTrue(iterator.hasNext());
            assertEquals(ELEMENTS[i], iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingSet() {
        // TODO
        assertFalse(true);
    }

    @Test
    void first() {
        putSomeToSet();
        assertEquals(ELEMENTS[0], set.first());
    }

    @Test
    void last() {
        putSomeToSet();
        assertEquals(ELEMENTS[ELEMENTS.length - 1], set.last());
    }

    @Test
    void lower() {
        putSomeToSet();
        assertEquals("a", set.lower("b"));
    }

    @Test
    void lowerNull() {
        putSomeToSet();
        assertNull(set.lower("a"));
    }

    @Test
    void lowerGreater() {
        putSomeToSet();
        assertEquals("c", set.lower("z"));
    }

    @Test
    void floor() {
        putSomeToSet();
        assertEquals("b", set.floor("b"));
    }

    @Test
    void floorNull() {
        putSomeToSet();
        assertNull(set.floor(""));
    }

    @Test
    void floorGreater() {
        putSomeToSet();
        assertEquals("c", set.floor("z"));
    }

    @Test
    void floorGap() {
        putSomeToSet();
        set.add("z");
        assertEquals("c", set.floor("h"));
    }

    @Test
    void ceiling() {
        putSomeToSet();
        assertEquals("b", set.ceiling("b"));
    }

    @Test
    void ceilingNull() {
        putSomeToSet();
        assertNull(set.ceiling("d"));
    }

    @Test
    void ceilingLower() {
        putSomeToSet();
        assertEquals("a", set.ceiling(""));
    }

    @Test
    void ceilingGap() {
        putSomeToSet();
        set.add("z");
        assertEquals("z", set.ceiling("h"));
    }

    @Test
    void higher() {
        putSomeToSet();
        assertEquals("c", set.higher("b"));
    }

    @Test
    void higherNull() {
        putSomeToSet();
        assertNull(set.ceiling("c"));
    }

    @Test
    void higherLower() {
        set.add("b");
        set.add("d");
        set.add("e");
        assertEquals("b", set.higher(""));
    }

    @Test
    void higherGap() {
        putSomeToSet();
        set.add("z");
        assertEquals("z", set.higher("h"));
    }

    @Test
    void isEmpty() {
        assertTrue(set.isEmpty());
        set.add("a");
        assertFalse(set.isEmpty());
    }

    @Test
    void containsTrue() {
        putSomeToSet();
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    @Test
    void containsFalse() {
        putSomeToSet();
        assertFalse(set.contains("q"));
        assertFalse(set.contains("w"));
        assertFalse(set.contains("e"));
    }

    @Test
    void containsTrash() {
        var i = new TreeSet<String>();
        i.contains(2);
        putSomeToSet();
        assertThrows(NoSuchElementException.class, () -> set.contains(Integer.valueOf(0)));
        assertThrows(NoSuchElementException.class, () -> set.contains(new MyTreeSetImplementationTest()));
    }
}