package com.example.phonebook;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implements queries to database needed by application as methods with self-explaining names.
 */
@NotNull
class PhonebookDatabaseController implements AutoCloseable {
    private static final String NAMES_TABLE = "Names";
    private static final String PHONES_TABLE = "Phones";
    private static final String CROSS_TABLE = "Phonebook";

    private final Connection connection;

    /**
     * Constructs new<code>PhonebookDatabaseController</code> using provided connection string.
     * Creates required tables in connected database in case they did not exist already
     * @throws SQLException in case of SQL error
     */
    public PhonebookDatabaseController(String connectionString) throws SQLException {
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

    private void insertName(String name) throws SQLException {
        String addEntryQuery = "INSERT OR IGNORE INTO " + NAMES_TABLE + " ('name') VALUES (?);";
        var preparedStatement = connection.prepareStatement(addEntryQuery);
        preparedStatement.setString(1, name);

        preparedStatement.executeUpdate();
    }

    private void insertPhone(String phone) throws SQLException {
        String addEntryQuery = "INSERT OR IGNORE INTO " + PHONES_TABLE + " (phone) VALUES (?);";
        var preparedStatement = connection.prepareStatement(addEntryQuery);
        preparedStatement.setString(1, phone);

        preparedStatement.executeUpdate();
    }

    /*
     Executes one preparedStatement with given parameter and query.
     Created only to evade copy paste for next two methods
      */
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

    private int getNameId(String name) throws SQLException {
        String query = "SELECT id FROM " + NAMES_TABLE + " WHERE name = ?";
        return getIntFromQuery(name, query);
    }

    private int getPhoneId(String phone) throws SQLException {
        String query = "SELECT id FROM " + PHONES_TABLE + " WHERE phone = ?";
        return getIntFromQuery(phone, query);
    }

    /**
     * Adds entry with specified name and phone number to phonebook
     * @return newly created entry id
     * @throws SQLException in case of SQL error
     */
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

    // query should be parametrized by one parameter
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

    /**
     * Finds all entries in database with given name
     * @throws SQLException if SQL error happens
     */
    public ArrayList<Entry> findByName(String name) throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE " + NAMES_TABLE + ".name = ? AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, name);
    }

    /**
     * Finds all entries in database with given phone number
     * @throws SQLException if SQL error happens
     */

    public ArrayList<Entry> findByPhone(String phoneNumber) throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE " + PHONES_TABLE + ".phone = ? AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, phoneNumber);
    }

    /**
     * Returns all entries presented in database
     * @throws SQLException if SQL error happens
     */
    public ArrayList<Entry> selectAll() throws SQLException {
        String query = "SELECT " + CROSS_TABLE + ".id, " + NAMES_TABLE + ".name, " + PHONES_TABLE + ".phone FROM "
                + " " + NAMES_TABLE + ", " + PHONES_TABLE + ", " + CROSS_TABLE
                + " WHERE ? = 'crutch' AND " + NAMES_TABLE + ".id = " + CROSS_TABLE + ".NameId AND " + PHONES_TABLE + ".id = " + CROSS_TABLE + ".PhoneId;";
        return selectByQuery(query, "crutch"); // eeh its better than creating another method
    }

    /**
     * Deletes entry with specified ID
     * @throws SQLException if SQL error happens
     */
    public void deleteById(int id) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE id = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, id);
        prepared.executeUpdate();
    }

    /**
     * Deletes all entries with specified name
     * @throws SQLException if SQL error happens
     */
    public void deleteByName(String name) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE nameId = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, getNameId(name));
        prepared.executeUpdate();
    }

    /**
     * Deletes all entries with specified phone number
     * @throws SQLException if SQL error happens
     */
    public void deleteByPhone(String phone) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE phoneId = ?;";
        var prepared = connection.prepareStatement(query);
        prepared.setInt(1, getPhoneId(phone));
        prepared.executeUpdate();
    }

    /**
     * Deletes all entries with specified name and phone number
     * @throws SQLException if SQL error happens
     */
    public void deleteByNamePhone(String name, String phone) throws SQLException {
        String query = "DELETE FROM " + CROSS_TABLE + " WHERE NameId = ? AND PhoneId = ?;";
        var prepared = connection.prepareStatement(query);
        // works well even if getNameId returns -1
        prepared.setInt(1, getNameId(name));
        prepared.setInt(2, getPhoneId(phone));
        prepared.executeUpdate();
    }

    /**
     * Sets new name to all entries containing specified name and phone number
     * @param name    name in entries that are about to be updated
     * @param phone   phone in entries that are about to be updated
     * @param newName new name to be set in selected entries
     * @throws SQLException if SQL error happens
     */
    public void updateName(String name, String phone, String newName) throws SQLException {
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

        prepared.executeUpdate();
    }

    /**
     * Sets new phone to all entries containing specified name and phone number
     * @param name    name in entries that are about to be updated
     * @param phone   phone in entries that are about to be updated
     * @param newPhone new phone to be set in selected entries
     * @throws SQLException if SQL error happens
     */
    public void updatePhone(String name, String phone, String newPhone) throws SQLException {
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

        prepared.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
