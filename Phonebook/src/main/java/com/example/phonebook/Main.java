package com.example.phonebook;

import java.util.Scanner;

/**
 * Minimalistic console application for storing pairs (Name, Phone number) in sqlite database/.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final String USAGE = "[-f databaseFile | -s sqliteConnectionString | -m]\n" +
            "Program uses sqlite and nothing is guaranteed if passed connection string uses different database system";
    private static final String DEFAULT_DB_NAME = "Phonebook.db";
    private static final String WELCOME_MESSAGE = "Print \"help\" for usage hints.";
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

    // types of database connection
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

    public static void main(String[] args) {
        var state = new ConnectState(args);
        String connectString = null; // it will be initialized in switch

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
            default:
            case DEFAULT:
                System.out.println("No database specified, " + DEFAULT_DB_NAME + " (in file) will be used");
                connectString = "jdbc:sqlite:" + DEFAULT_DB_NAME;
                break;
        }


        try (var db = new PhonebookDatabaseController(connectString)) {
            System.out.println("Successfully connected.");
            System.out.println(WELCOME_MESSAGE);

            boolean exit = false;
            while (!exit) {
                System.out.print("> ");
                String inputLine = SCANNER.nextLine();
                if ("0".equals(inputLine) || "exit".equals(inputLine)) {
                    exit = true;

                } else if ("1".equals(inputLine) || "add".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    String phone = SCANNER.nextLine();
                    db.addEntry(name, phone);

                } else if ("2".equals(inputLine) || "findn".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    var entries = db.findByName(name);
                    for (Entry i : entries) {
                        System.out.print(i.id);
                        System.out.println(" | " + i.phone);
                    }

                } else if ("3".equals(inputLine) || "findp".equals(inputLine)) {
                    String phone = SCANNER.nextLine();
                    var entries = db.findByPhone(phone);
                    for (Entry i : entries) {
                        System.out.print(i.id);
                        System.out.println(" | " + i.name);
                    }

                } else if ("4".equals(inputLine) || "del".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    String phone = SCANNER.nextLine();
                    db.deleteByNamePhone(name, phone);

                } else if ("5".equals(inputLine) || "un".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    String phone = SCANNER.nextLine();
                    String newName = SCANNER.nextLine();
                    db.updateName(name, phone, newName);

                } else if ("6".equals(inputLine) || "up".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    String phone = SCANNER.nextLine();
                    String newPhone = SCANNER.nextLine();
                    db.updatePhone(name, phone, newPhone);

                } else if ("7".equals(inputLine) || "show".equals(inputLine)) {
                    var entries = db.selectAll();
                    for (Entry i : entries) {
                        System.out.println(i.toString());
                    }
                } else if ("8".equals(inputLine) || "di".equals(inputLine)) {
                    int id = Integer.parseInt(SCANNER.nextLine());
                    db.deleteById(id);

                } else if ("9".equals(inputLine) || "dn".equals(inputLine)) {
                    String name = SCANNER.nextLine();
                    db.deleteByName(name);

                } else if ("10".equals(inputLine) || "dp".equals(inputLine)) {
                    String phone = SCANNER.nextLine();
                    db.deleteByPhone(phone);

                } else if ("help".equals(inputLine)) {
                    System.out.println(HELP_MESSAGE);
                } else {
                    System.out.println("Incorrect usage. Type \"help\" for usage hints.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("This shouldn't have happen ¯\\_(ツ)_/¯ ");
            System.out.println("Maybe database is broken, or maybe it's a bug");
        }
    }
}