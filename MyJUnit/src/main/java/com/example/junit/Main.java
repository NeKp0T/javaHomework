package com.example.junit;

import com.example.junit.logic.TestRunner;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide one argument");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            String argument = args[i];
            if (argument.length() > 0 && argument.charAt(0) == '-') {
                if ("-h".equals(argument) || "--help".equals(argument)) {
                    printHelp();
                }
            }
        }

        var tr = new TestRunner();

        File f = new File("./src/main/java/");
        try {
            tr.test(f, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println(help);
    }

    private static final String help = "USAGE:" +
            "    appname [-h | -t path | path]*";
}
