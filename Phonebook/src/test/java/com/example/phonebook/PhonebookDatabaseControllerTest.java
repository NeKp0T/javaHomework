package com.example.phonebook;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class PhonebookDatabaseControllerTest {

    PhonebookDatabaseController db;

    private static final Entry[] INITIAL_ENTRIES = {
            new Entry(1, "aaa", "000"),
            new Entry(2, "aaa", "000"),
            new Entry(3, "aaa", "111"),

            new Entry(4, "bbb", "000"),
            new Entry(5, "bbb", "404"),

            new Entry(6, "ccc", "000"),
            new Entry(7, "ccc", "404")
    };

    @BeforeEach
    void initDB() throws SQLException {
        db = new PhonebookDatabaseController("jdbc:sqlite:"); // temporary database
    }

    @AfterEach
    void destroyDB() throws Exception {
        db.close();
    }

    private void addSomeToDB() throws SQLException {
        for (Entry i : INITIAL_ENTRIES) {
            db.addEntry(i.name, i.phone);
        }
    }

    @Test
    void addEntryTest() throws SQLException {
        assertEquals(1, db.addEntry("aaa", "000"));
    }

    @Test
    void addEntryMatchingFieldsTest() throws SQLException {
        assertEquals(1, db.addEntry("aaa", "000"));
        assertEquals(2, db.addEntry("bbb", "000"));
        assertEquals(3, db.addEntry("aaa", "111"));
        assertEquals(4, db.addEntry("aaa", "000"));
    }

    @Test
    void addSomeTest() throws SQLException {
        addSomeToDB();
    }

    @Test
    void findByNameTest() throws SQLException {
        addSomeToDB();
        ArrayList<Entry> entries = db.findByName("aaa");

        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x -> "aaa".equals(x.name))
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void findByPhoneTest() throws SQLException {
        addSomeToDB();
        ArrayList<Entry> entries = db.findByPhone("000");
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                            .filter(x -> "000".equals(x.phone))
                            .collect(Collectors.toList())),
                entries);
    }

    @Test
    void selectAllTest() throws SQLException {
        addSomeToDB();
        assertEquals(new ArrayList<Entry>(Arrays.asList(INITIAL_ENTRIES)), db.selectAll());
    }

    @Test
    void deleteByIdTest() throws SQLException {
        addSomeToDB();
        db.deleteById(3);
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x -> x.id != 3)
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void deleteByNameTest() throws SQLException {
        addSomeToDB();
        db.deleteByName("bbb");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x -> !"bbb".equals(x.name))
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void deleteByPhoneTest() throws SQLException {
        addSomeToDB();
        db.deleteByPhone("000");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x -> !"000".equals(x.phone))
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void deleteByNamePhoneTest() throws SQLException {
        addSomeToDB();
        db.deleteByNamePhone("bbb", "404");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x ->  !"bbb".equals(x.name) || !"404".equals(x.phone))
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void deleteMultipleByNamePhoneTest() throws SQLException {
        addSomeToDB();
        db.deleteByNamePhone("aaa", "000");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .filter(x ->  !"aaa".equals(x.name) || !"000".equals(x.phone))
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void updateNameTest() throws SQLException {
        addSomeToDB();
        db.updateName("ccc", "000", "aaa");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .map(x -> {
                            if ("ccc".equals(x.name) && "000".equals(x.phone)) {
                                return new Entry(x.id, "aaa", x.phone);
                            }
                            return x;
                        })
                        .collect(Collectors.toList())),
                entries);
    }

    @Test
    void updatePhoneTest() throws SQLException {
        addSomeToDB();
        db.updatePhone("ccc", "000", "123");
        ArrayList<Entry> entries = db.selectAll();
        assertEquals(
                new ArrayList<>(Arrays.stream(INITIAL_ENTRIES)
                        .map(x -> {
                            if ("ccc".equals(x.name) && "000".equals(x.phone)) {
                                return new Entry(x.id, x.name, "123");
                            }
                            return x;
                        })
                        .collect(Collectors.toList())),
                entries);
    }
}