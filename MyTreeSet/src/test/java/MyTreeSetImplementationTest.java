import com.example.treeSet.MyTreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.treeSet.MyTreeSetImplementation;

import java.util.*;


class MyTreeSetImplementationTest {

    MyTreeSetImplementation<String> defaultSet;

    private static String[] elements =  new String[]{"a", "b", "c"};
    private void putbcabToSet(MyTreeSet<String> set) {
        set.add("b");
        set.add("c");
        set.add("a");
        set.add("b");
    }

    @BeforeEach
    void init() {
        defaultSet = new MyTreeSetImplementation<>();
    }

    @Test
    void addTrue() {
        addTrue(defaultSet);
    }

    void addTrue(MyTreeSet<String> set) {
        assertTrue(set.add("a"));
        assertTrue(set.add("b"));
        assertTrue(set.add("c"));
    }

    @Test
    void addFalse() {
        addFalse(defaultSet);
    }

    void addFalse(MyTreeSet<String> set) {
        assertTrue(set.add("a"));
        assertFalse(set.add("a"));
    }

    @Test
    void addComplicated() {
        addComplicated(defaultSet);
    }

    void addComplicated(MyTreeSet<String> set) {
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
    void sizeBasic() {
        sizeBasic(defaultSet);
    }

    void sizeBasic(MyTreeSet<String> set) {
        assertEquals(0, set.size());
        set.add("a");
        assertEquals(1, set.size());
    }

    @Test
    void sizeCollisions() {
        sizeCollisions(defaultSet);
    }

    void sizeCollisions(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals(3, set.size());
    }

    @Test
    void iteratorNextAndHasNext() {
        iteratorNextAndHasNext(defaultSet);
    }

    void iteratorNextAndHasNext(MyTreeSet<String> set) {
        putbcabToSet(set);
        var iterator = set.iterator();
        for (int i = 0; i < 3; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(elements[i], iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingIterator() {
        descendingIterator(defaultSet);
    }

    void descendingIterator(MyTreeSet<String> set) {
        putbcabToSet(set);
        var iterator = set.descendingIterator();
        for (int i = 2; i >= 0; i--) {
            assertTrue(iterator.hasNext());
            assertEquals(elements[i], iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void first() {
        first(defaultSet);
    }

    void first(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals(elements[0], set.first());
    }

    @Test
    void last() {
        last(defaultSet);
    }

    void last(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals(elements[elements.length - 1], set.last());
    }

    @Test
    void lower() {
        lower(defaultSet);
    }

    void lower(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("a", set.lower("b"));
    }

    @Test
    void lowerNull() {
        lowerNull(defaultSet);
    }

    void lowerNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.lower("a"));
    }

    @Test
    void lowerGreater() {
        lowerGreater(defaultSet);
    }

    void lowerGreater(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.lower("z"));
    }

    @Test
    void floor() {
        floor(defaultSet);
    }

    void floor(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("b", set.floor("b"));
    }

    @Test
    void floorNull() {
        floorNull(defaultSet);
    }

    void floorNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.floor(""));
    }

    @Test
    void floorGreater() {
        floorGreater(defaultSet);
    }

    void floorGreater(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.floor("z"));
    }

    @Test
    void floorGap() {
        floorGap(defaultSet);
    }

    void floorGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("c", set.floor("h"));
    }

    @Test
    void ceiling() {
        ceiling(defaultSet);
    }

    void ceiling(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("b", set.ceiling("b"));
    }

    @Test
    void ceilingNull() {
        ceilingNull(defaultSet);
    }

    void ceilingNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.ceiling("d"));
    }

    @Test
    void ceilingLower() {
        ceilingLower(defaultSet);
    }

    void ceilingLower(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("a", set.ceiling(""));
    }

    @Test
    void ceilingGap() {
        ceilingGap(defaultSet);
    }

    void ceilingGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("z", set.ceiling("h"));
    }

    @Test
    void higher() {
        higher(defaultSet);
    }

    void higher(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.higher("b"));
    }

    @Test
    void higherNull() {
        higherNull(defaultSet);
    }

    void higherNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.higher("c"));
    }

    @Test
    void higherLower() {
        higherLower(defaultSet);
    }

    void higherLower(MyTreeSet<String> set) {
        set.add("b");
        set.add("d");
        set.add("e");
        assertEquals("b", set.higher(""));
    }

    @Test
    void higherGap() {
        higherGap(defaultSet);
    }

    void higherGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("z", set.higher("h"));
    }

    @Test
    void isEmpty() {
        isEmpty(defaultSet);
    }

    void isEmpty(MyTreeSet<String> set) {
        assertTrue(set.isEmpty());
        set.add("a");
        assertFalse(set.isEmpty());
    }

    @Test
    void containsTrue() {
        containsTrue(defaultSet);
    }

    void containsTrue(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    @Test
    void containsFalse() {
        containsFalse(defaultSet);
    }

    void containsFalse(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertFalse(set.contains("q"));
        assertFalse(set.contains("w"));
        assertFalse(set.contains("e"));
    }

    @Test
    @SuppressWarnings({"ResultOfMethodCallIgnored", "SuspiciousMethodCalls"})
    void containsTrash() {
        containsTrash(defaultSet);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "SuspiciousMethodCalls"})
    void containsTrash(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertThrows(ClassCastException.class, () -> set.contains(2));
    }


//    @Test
//    void differentComparatorTests() {
//        elements = new String[]{"c","b","a"};
////        var set = new MyTreeSetImplementation<String>(Comparator.reverseOrder());
//        addTrue(new MyTreeSetImplementation<String>(Comparator.reverseOrder()));
//        addFalse(new MyTreeSetImplementation<String>(Comparator.reverseOrder()));
//    }
//
//
//    @Test
//    void descendingSet() {
////        elements = new String[]{"c","b","a"};
//        assertFalse(true);
//    }
}