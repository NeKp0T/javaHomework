package com.example.phonebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

final class Entry {
    public final String name;
    public final String phone;
    public final int id;

    public Entry(int id, String nameString, String phoneNumberString) {
        this.id = id;
        name = nameString;
        phone = phoneNumberString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            Entry that = (Entry) obj;
            return name.equals(that.name) && phone.equals(that.phone);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + phone;
    }
}

class DBController implements AutoCloseable {
    static final String NAMES_TABLE = "NamesT";
    static final String PHONES_TABLE = "Phones";
    static final String CROSS_TABLE = "Phonebook";

    private final Connection connection;

    public DBController(String connectionString) throws SQLException {
        connection = DriverManager.getConnection(connectionString);
        String createNamesTableQuery = "CREATE TABLE IF NOT EXISTS " + NAMES_TABLE + " (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL UNIQUE \n"
                + ");";
        connection.createStatement().execute(createNamesTableQuery);
        String createPhoneTableQuery = "CREATE TABLE IF NOT EXISTS " + PHONES_TABLE + " (\n"
                + " id integer PRIMARY KEY,\n"
                + " phone text NOT NULL UNIQUE\n"
                + ");";
        connection.createStatement().executeUpdate(createPhoneTableQuery);
        String createCrossTableQuery = "CREATE TABLE IF NOT EXISTS " + CROSS_TABLE + " (\n"
                + " id integer PRIMARY KEY,\n"
                + " nameId integer,\n"
                + " phoneId integer\n"
                + ");";
        connection.createStatement().executeUpdate(createCrossTableQuery);
    }

    private boolean insertName(String name) throws SQLException {
        String addEntryQuery = "INSERT OR IGNORE INTO " + NAMES_TABLE + " ('name') VALUES (?);";
        var preparedStatement = connection.prepareStatement(addEntryQuery);
        preparedStatement.setString(1, name);

        return preparedStatement.executeUpdate() > 0;
    }

    private boolean insertPhone(String phone) throws SQLException {
        String addEntryQuery = "INSERT OR IGNORE INTO " + PHONES_TABLE + " (phone) VALUES (?);";
        var preparedStatement = connection.prepareStatement(addEntryQuery);
        preparedStatement.setString(1, phone);

        return preparedStatement.executeUpdate() > 0;
    }

    private int getIntFromQuery(String parameter, String query) throws SQLException {
        var preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, parameter);

        ResultSet set = preparedStatement.executeQuery();
        if (set.next()) {
            return set.getInt(1);
        } else {
            return -1;
        }
    }

    /*
       Returns id of given name in NAMES_TABLE, or -1 if given namesis not present.
     */
    private int getNameId(String name) throws SQLException {
        String query = "SELECT id FROM " + NAMES_TABLE + " WHERE name = ?";
        return getIntFromQuery(name, query);
    }

    /*
       Returns id of given phone in PHONES_TABLE, or -1 if given phone is not present.
     */
    private int getPhoneId(String phone) throws SQLException {
        String query = "SELECT id FROM " + PHONES_TABLE + " WHERE phone = ?";
        return getIntFromQuery(phone, query);
    }

    /* Returns id of new entry */
    public int addEntry(String name, String phone) throws SQLException {
        insertPhone(phone);
        insertName(name);

        int nameId = getNameId(name);
        int phoneId = getPhoneId(phone);

        String addEntryQuery = "INSERT INTO " + CROSS_TABLE + "(nameId, phoneId) VALUES(?,?);";
        var preparedStatement = connection.prepareStatement(addEntryQuery);
        preparedStatement.setInt(1, nameId);
        preparedStatement.setInt(2, phoneId);
        preparedStatement.executeUpdate();

        ResultSet keys = preparedStatement.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1); // generated id
        } else {
            throw new SQLException("Add entry succeeded, but, no ID obtained.");
        }
    }

    private ArrayList<Entry> selectByQuery(String query, String parameter) throws SQLException {
        var prepared = connection.prepareStatement(query);
        prepared.setString(1, parameter);
        ResultSet result = prepared.executeQuery();

        var entries =  new ArrayList<Entry>();
        while (result.next()) {
            entries.add(new Entry(result.getInt(1),
                    result.getString(2),
                    result.getString("phone")));
        }
        return entries;
    }

    public ArrayList<Entry> findByName(String name) throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE " + NAMES_TABLE + ".name = ? AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, name);
    }

    public ArrayList<Entry> findByPhone(String phoneNumber) throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE " + PHONES_TABLE + ".phone = ? AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, phoneNumber);
    }

    public ArrayList<Entry> selectAll() throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE ? = 'crutch' AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, "crutch");
    }

    public int deleteById(int id) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE id = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, id);
        return prepared.executeUpdate();
    }

    public int deleteByName(String name) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE nameId = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, getNameId(name));
        return prepared.executeUpdate();
    }

    public int deleteByPhone(String phone) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE phoneId = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, getPhoneId(phone));
        return prepared.executeUpdate();
    }

    public int deleteByNamePhone(String name, String phone) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE NameId = ? AND PhoneId = ?;";
        var prepared = connection.prepareStatement(query);
        // works well even if getNameId returns -1
        prepared.setInt(1, getNameId(name));
        prepared.setInt(2, getPhoneId(phone));
        return prepared.executeUpdate();
    }

    /**
     * @return if any entry was updated.
     */
    public int updateName(String name, String phone, String newName) throws SQLException {
        insertName(newName);
        String query = "UPDATE " + CROSS_TABLE + " SET "
                + "nameId = ? \n"
                + "WHERE nameId = ? AND phoneId = ?;";
        var prepared = connection.prepareStatement(query);

        int nameId = getNameId(name);
        int phoneId = getPhoneId(phone);
        int newNameId = getNameId(newName);
        prepared.setInt(1, newNameId);
        prepared.setInt(2, nameId);
        prepared.setInt(3, phoneId);

        return prepared.executeUpdate();
    }

    /**
     * @return if any entry was updated.
     */
    public int updatePhone(String name, String phone, String newPhone) throws SQLException {
        insertPhone(newPhone);
        String query = "UPDATE " + CROSS_TABLE + " SET "
                + "phoneId = ? \n"
                + "WHERE nameId = ? AND phoneId = ?;";
        var prepared = connection.prepareStatement(query);

        int nameId = getNameId(name);
        int phoneId = getPhoneId(phone);
        int newPhoneId = getPhoneId(newPhone);
        prepared.setInt(1, newPhoneId);
        prepared.setInt(2, nameId);
        prepared.setInt(3, phoneId);

        return prepared.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

public class Main {

    static Scanner scanner = new Scanner(System.in);

    private static final String USAGE = "[-f databaseFile | -s sqliteConnectionString | -m]\n" +
            "Program uses sqlite and nothing is guaranteed if passed connection string uses different database system";
    private static final String DEFAULT_DB_NAME = "Phonebook.db";
    private static final String WELCOME_MESSAGE = "Print \".help\" for usage hints.";
    private static final String HELP_MESSAGE = "[COMMAND] [ARGUMENTS...]\n" +
            "Command and each argument take one whole line.\n" +
//            "Commands that modify phonebook show number of modified entries.\n" +
//              ^ they showed executeUpdate result, but it seems it isn't what I thought it is
            "\n" +
            "List of commands:\n" +
            "help                   display this message\n" +
            "[0, exit]              exit\n" +
            "[1, add] name phone    add new entry\n" +
            "[2, findn] name        find all entries with given name\n" +
            "[3, findp] phone       find all entries with given phone\n" +
            "[4, del] name phone    delete given entry\n" +
            "[5, un] name phone newName     update name in given entry \n" +
            "[6, up] name phone newPhone    update phone in given entry \n" +
            "[7, show]              show all entries\n" +
            "[8, di] id             delete entry by id\n" +
            "[9, dn] name           delete entries with given name\n" +
            "[10, dp]  phone        delete entries with given phone\n";

    private enum DBType {DEFAULT, BY_FILE, BY_CONNECT_STRING, IN_MEMORY, ERROR}

    private static class ConnectState {
        public final DBType type;
        public final String s;

        ConnectState(String[] args) {
            if (args.length == 0) {
                type = DBType.DEFAULT;
                s = null;
            } else if ("-m".equals(args[0])) {
                type = DBType.IN_MEMORY;
                s = null;
            } else if (args.length == 1) {
                type = DBType.ERROR;
                s = null;
            } else if ("-f".equals(args[0])) {
                type = DBType.BY_FILE;
                s = args[1];
            } else if ("-s".equals(args[0])) {
                type = DBType.BY_CONNECT_STRING;
                s = args[1];
            } else {
                type = DBType.ERROR;
                s = null;
            }
        }
    }

    private static void printlnDeleted(int deleted) {
        System.out.print("deleted ");
        System.out.print(deleted);
        System.out.print(" entries\n");
    }

    public static void main(String[] args) {
        var state = new ConnectState(args);
        String connectString;

        switch (state.type) {
            case BY_FILE:
                System.out.println("Will use database in file " + state.s + ".");
                connectString = "jdbc:sqlite:" + state.s;
                break;
            case BY_CONNECT_STRING:
                System.out.println("Will use database by connect string " + state.s + ".");
                connectString = state.s;
                break;
            case IN_MEMORY:
                System.out.println("Will use a transient in-memory database.");
                connectString = "jdbc:sqlite::memory";
                break;
            case ERROR:
                System.out.println(USAGE);
                return;
            case DEFAULT:
                System.out.println("No database specified, " + DEFAULT_DB_NAME + " (in file) will be used");
                connectString = "jdbc:sqlite:" + DEFAULT_DB_NAME;
                break;
        }


        try (var db = new DBController("jdbc:sqlite:./s.db")) {
            System.out.println("Succesfully connected.");
            System.out.println(WELCOME_MESSAGE);

            boolean exit = false;
            while (!exit) {
                System.out.print("> ");
                String inputLine = scanner.nextLine();
                if ("0".equals(inputLine) || "exit".equals(inputLine)) {
                    exit = true;

                } else if ("1".equals(inputLine) || "add".equals(inputLine)) {
                    String name = scanner.nextLine();
                    String phone = scanner.nextLine();
                    db.addEntry(name, phone);

                } else if ("2".equals(inputLine) || "findn".equals(inputLine)) {
                    String name = scanner.nextLine();
                    var entries = db.findByName(name);
                    for (Entry i : entries) {
                        System.out.print(i.id);
                        System.out.println(" | " + i.phone);
                    }

                } else if ("3".equals(inputLine) || "findp".equals(inputLine)) {
                    String phone = scanner.nextLine();
                    var entries = db.findByPhone(phone);
                    for (Entry i : entries) {
                        System.out.print(i.id);
                        System.out.println(" | " + i.name);
                    }

                } else if ("4".equals(inputLine) || "del".equals(inputLine)) {
                    String name = scanner.nextLine();
                    String phone = scanner.nextLine();
                    db.deleteByNamePhone(name, phone);

                } else if ("5".equals(inputLine) || "un".equals(inputLine)) {
                    String name = scanner.nextLine();
                    String phone = scanner.nextLine();
                    String newName = scanner.nextLine();
                    db.updateName(name, phone, newName);

                } else if ("6".equals(inputLine) || "up".equals(inputLine)) {
                    String name = scanner.nextLine();
                    String phone = scanner.nextLine();
                    String newPhone = scanner.nextLine();
                    db.updatePhone(name, phone, newPhone);

                } else if ("7".equals(inputLine) || "show".equals(inputLine)) {
                    var entries = db.selectAll();
                    for (Entry i : entries) {
                        System.out.println(i.toString());
                    }
                } else if ("8".equals(inputLine) || "di".equals(inputLine)) {
                    Integer id = Integer.valueOf(scanner.nextLine());
                    db.deleteById(id);

                } else if ("9".equals(inputLine) || "dn".equals(inputLine)) {
                    String name = scanner.nextLine();
                    db.deleteByName(name);

                } else if ("10".equals(inputLine) || "dp".equals(inputLine)) {
                    String phone = scanner.nextLine();
                    db.deleteByPhone(phone);

                } else if ("help".equals(inputLine)) {
                    System.out.println(HELP_MESSAGE);
                } else {
                    System.out.println("Incorrect usage. Type \"help\" for usage.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}