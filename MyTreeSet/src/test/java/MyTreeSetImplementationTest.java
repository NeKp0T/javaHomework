import com.example.treeSet.MyTreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.treeSet.MyTreeSetImplementation;

import java.lang.reflect.Executable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;


class MyTreeSetImplementationTest {

    MyTreeSetImplementation<String> defaultSet;

    private static String[] elements;
    private void putbcabToSet(MyTreeSet<String> set) {
        set.add("b");
        set.add("c");
        set.add("a");
        set.add("b");
    }

    @BeforeEach
    void init() {
        defaultSet = new MyTreeSetImplementation<>();
        elements = new String[]{"a", "b", "c"};
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
    void isEmptyTrue() {
        assertTrue(defaultSet.isEmpty());
    }

    @Test
    void isEmptyFalse() {
        defaultSet.add("a");
        assertFalse(defaultSet.isEmpty());
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


    @Test
    void differentComparatorTests() {
        elements = new String[]{"c", "b", "a"};
        runMostTests(() -> new MyTreeSetImplementation<>(Comparator.reverseOrder()));
        descendingFloorCeilEtc(() -> (new MyTreeSetImplementation<String>()).descendingSet());
    }

    @Test
    void descendingSet() {
        elements = new String[]{"c", "b", "a"};
        runMostTests(() -> (new MyTreeSetImplementation<String>()).descendingSet());
        descendingFloorCeilEtc(() -> (new MyTreeSetImplementation<String>()).descendingSet());
    }

    @Test
    void descendingSetSquared() {
        runMostTests(() -> (new MyTreeSetImplementation<String>()).descendingSet().descendingSet());
    }

    void runMostTests(Supplier<MyTreeSet<String>> setGenerator) {
        addComplicated(setGenerator.get());
        addFalse(setGenerator.get());
        addTrue(setGenerator.get());
//        ceiling(setGenerator.get());
//        ceilingGap(setGenerator.get());
//        ceilingLower(setGenerator.get());
//        ceilingNull(setGenerator.get());
        containsFalse(setGenerator.get());
        containsTrash(setGenerator.get());
        containsTrue(setGenerator.get());
        descendingIterator(setGenerator.get());
        first(setGenerator.get());
//        floor(setGenerator.get());
//        floorGap(setGenerator.get());
//        floorGreater(setGenerator.get());
//        floorNull(setGenerator.get());
//        higher(setGenerator.get());
//        higherGap(setGenerator.get());
//        higherLower(setGenerator.get());
//        higherNull(setGenerator.get());
        iteratorNextAndHasNext(setGenerator.get());
        last(setGenerator.get());
//        lower(setGenerator.get());
//        lowerGreater(setGenerator.get());
//        lowerNull(setGenerator.get());
        sizeBasic(setGenerator.get());
        sizeCollisions(setGenerator.get());
        sizeCollisions(setGenerator.get());
    }

    void descendingFloorCeilEtc(Supplier<MyTreeSet<String>> setGenerator) {
        descendingHigher(setGenerator.get());
        descendingHigherNull(setGenerator.get());
        descendingHigherGreater(setGenerator.get());
        descendingCeiling(setGenerator.get());
        descendingCeilingNull(setGenerator.get());
        descendingCeilingGreater(setGenerator.get());
        descendingCeilingGap(setGenerator.get());
        descendingFloor(setGenerator.get());
        descendingFloorNull(setGenerator.get());
        descendingFloorLower(setGenerator.get());
        descendingFloorGap(setGenerator.get());
        descendingLower(setGenerator.get());
        descendingLowerNull(setGenerator.get());
        descendingLowerLower(setGenerator.get());
        descendingLowerGap(setGenerator.get());
    }

    void descendingHigher(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("a", set.higher("b"));
    }

    void descendingHigherNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.higher("a"));
    }

    void descendingHigherGreater(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.higher("z"));
    }

    void descendingCeiling(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("b", set.ceiling("b"));
    }

    void descendingCeilingNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.ceiling(""));
    }

    void descendingCeilingGreater(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.ceiling("z"));
    }

    void descendingCeilingGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("c", set.ceiling("h"));
    }

    void descendingFloor(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("b", set.floor("b"));
    }

    void descendingFloorNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.floor("d"));
    }

    void descendingFloorLower(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("a", set.floor(""));
    }

    void descendingFloorGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("z", set.floor("h"));
    }

    void descendingLower(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertEquals("c", set.lower("b"));
    }

    void descendingLowerNull(MyTreeSet<String> set) {
        putbcabToSet(set);
        assertNull(set.lower("c"));
    }

    void descendingLowerLower(MyTreeSet<String> set) {
        set.add("b");
        set.add("d");
        set.add("e");
        assertEquals("b", set.lower(""));
    }

    void descendingLowerGap(MyTreeSet<String> set) {
        putbcabToSet(set);
        set.add("z");
        assertEquals("z", set.lower("h"));
    }
}

