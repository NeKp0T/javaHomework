import com.example.trie.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Executable;

import static org.junit.jupiter.api.Assertions.*;


class TrieTest {
    private Trie trie;

    @BeforeEach
    void initTrie() {
        trie = new Trie();
    }

    @Test
    void addAndContainsBasic() {
        trie.add("qwerty");
        assertTrue(trie.contains("qwerty"));
        assertFalse(trie.contains("ytrewq"));
    }

    @Test
    void addReturnValue() {
        assertTrue(trie.add("E"));
        assertFalse(trie.add("E"));
    }

    @Test
    void addAdvanced() {
        assertFalse(trie.contains(""));
        trie.add("");
        assertTrue(trie.contains(""));
        trie.add("aa");
        assertTrue(trie.contains("aa"));
        assertFalse(trie.contains("a"));
        assertFalse(trie.contains("aaa"));
    }

    @Test
    void addAndContainsWeirdSymbols() {
        String punctuation = "\"',./<>?\\|!@#$%^&*()_+";
        String rus = "абвгдеёжзийклмнопрстуфхцчшщъыбэюя";
        String arabic = "غظضذخثتشرقصفعسنملكيطحزوهدجبأ";
        String emoji = "\uD83D\uDD25\uD83D\uDCAF";
        trie.add(punctuation);
        trie.add(rus);
        trie.add(arabic);
        trie.add(emoji);
        assertTrue(trie.contains(punctuation));
        assertTrue(trie.contains(rus));
        assertTrue(trie.contains(arabic));
        assertTrue(trie.contains(emoji));
    }

    @Test
    void contains() {
        trie.add("aaaaa");
        assertTrue(trie.contains("aaaaa"));
        assertFalse(trie.contains("aaaa"));
        assertFalse(trie.contains("aaaaaa"));
    }

    @Test
    void containsBigTrie() {
        for (int i = 0; i < 1000; i++) {
            var s = Integer.toBinaryString(i);
            assertFalse(trie.contains(s));
            assertTrue(trie.add(s));
            assertFalse(trie.add(s));
            assertTrue(trie.contains(s));
        }
    }

    @Test
    void remove() {
        assertFalse(trie.remove("q"));
        trie.add("q");
        assertTrue(trie.remove("q"));
    }

    @Test
    void removeAdvanced() {
        trie.add("qwer"); // { qwer }
        trie.add("qwer"); // { qwer }
        assertTrue(trie.remove("qwer")); // {}
        trie.add("qw"); // { qw }
        assertFalse(trie.contains("qwer"));
        assertFalse(trie.remove("qwer")); // { qw }
        assertFalse(trie.remove("qwer")); // { qw }
        assertFalse(trie.contains("qwer"));
        trie.add("qwer"); // { qw, qwer}
        assertTrue(trie.remove("qw")); // { qwer }
        assertFalse(trie.remove("qw")); // { qwer }
    }

    @Test
    void size() {
        assertEquals(0, trie.size());
        trie.add("qwer");
        assertEquals(1, trie.size());
        trie.add("qwer");
        assertEquals(1, trie.size());
        trie.add("qw");
        assertEquals(2, trie.size());
    }


    @Test
    void sizeRemove() {
        trie.add("qwer");
        assertEquals(1, trie.size());
        trie.add("qwer");
        assertEquals(1, trie.size());
        trie.remove("qwer");
        assertEquals(0, trie.size());
    }

    @Test
    void howManyStartsWithPrefix() {
        assertEquals(0, trie.howManyStartsWithPrefix(""));
        trie.add("qwer");
        assertEquals(1, trie.howManyStartsWithPrefix("qw"));
        trie.add("qw");
        assertEquals(2, trie.howManyStartsWithPrefix("qw"));
        assertEquals(2, trie.howManyStartsWithPrefix("q"));
        assertEquals(1, trie.howManyStartsWithPrefix("qwe"));
        assertEquals(1, trie.howManyStartsWithPrefix("qwer"));
    }

    @Test
    void serializeDeserialize() throws IOException {
        trie.add("qqaa");
        trie.add("qqbb");
        trie.add("");
        trie.add("qq");
        var out = new ByteArrayOutputStream();
        trie.serialize(out);

        var in = new ByteArrayInputStream(out.toByteArray());
        var trieRead = new Trie();
        trieRead.add("q");
        trieRead.deserialize(in);
        assertTrue(trieRead.contains("qqaa"));
        assertTrue(trieRead.contains("qqbb"));
        assertTrue(trieRead.contains(""));
        assertTrue(trieRead.contains("qq"));
        assertFalse(trieRead.contains("q"));
        assertFalse(trieRead.contains("qqb"));

        assertEquals(trie.howManyStartsWithPrefix(""),
                trieRead.howManyStartsWithPrefix(""));
        assertEquals(trie.howManyStartsWithPrefix("q"),
                trieRead.howManyStartsWithPrefix("q"));
        assertEquals(trie.howManyStartsWithPrefix("qq"),
                trieRead.howManyStartsWithPrefix("qq"));
        assertEquals(trie.howManyStartsWithPrefix("qqa"),
                trieRead.howManyStartsWithPrefix("qqa"));
    }

    @Test
    void addThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
    }

    @Test
    void containsThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    void removeThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> trie.remove(null));
    }

    @Test
    void serializeThrowsTest() {
        assertThrows(NullPointerException.class, () -> trie.serialize(null));
    }

    @Test
    void deserializeThrowsTest() {
        assertThrows(NullPointerException.class, () -> trie.deserialize(null));
    }
}
