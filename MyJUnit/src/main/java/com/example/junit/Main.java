package com.example.junit;

import com.example.junit.logic.TestRunner;

import java.io.File;
import java.io.IOException;

/**
 * A console application that runs tests in all classes in specified folders.
 *
 * Takes arguments in format "-h | [-t path | path]*"
 */
public class Main {

    /**
     * Runs console application
     * @param args arguments in format "-h | [-t path | path]*"
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide at least one argument");
            printHelp();
            return;
        }
        if (args.length == 1 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
            printHelp();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if ("-t".equals(args[i])) {
                i++;
                if (i >= args.length) {
                    System.out.println("Test directory not specified");
                    break;
                }
            }
            String path = args[i];

            var tester = new TestRunner();
            var f = new File(path);
            if (!f.exists()) {
                System.out.println("File does not exist: " + path);
                continue;
            }
            if (!f.isDirectory()) {
                System.out.println("File is not a directory: " + path);
                continue;
            }
            try {
                tester.test(f, System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void printHelp() {
        System.out.println(help);
    }

    private static final String help = "USAGE:" +
            "    appname -h | [-t path | path]*";
}
