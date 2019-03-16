package com.example.reflector;

import java.io.*;

/**
 * Class provides methods for presenting some information about runtime classes
 * in human-readable form.
 * Ignores annotations.
 * Interfaces and enums are not supported and may cause undefined behavior.
 */
public class Reflector {
    /**
     * Prints possible implementation of provided class into file ProvidedClassName.java.
     * @param someClass class to print
     * @throws IOException if exception happens while writing
     */
    public static void printStructure(Class<?> someClass) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(someClass.getName() + ".java"))) {
            printStructure(someClass, writer);
        }
    }

    /**
     * Prints possible implementation of provided class to <code>System.out</code>.
     * @param someClass class to print
     * @throws IOException if exception happens while writing
     */
    public static void printStructureToConsole(Class<?> someClass) throws IOException {
        var writer = new PrintWriter(System.out);
        printStructure(someClass, writer);
        writer.flush();
    }

    /**
     * Prints possible implementation of provided class to provided <code>Writer</code>.
     * @param someClass class to print
     * @param writer <code>Writer</code> to write to
     * @throws IOException if exception happens while writing
     */
    public static void printStructure(Class<?> someClass, Writer writer) throws IOException {
        ClassStructurePrinter.writeClass(someClass, writer);
    }

    /**
     * Prints differences in methods and fields of provided classes to <class>System.out</class>.
     * @param someClass first class
     * @param otherClass second class
     * @throws IOException if exception happens while writing
     */
    public static void printDifferenceToConsole(Class<?> someClass, Class<?> otherClass) throws IOException {
        var writer = new PrintWriter(System.out);
        printDifference(someClass, otherClass, writer);
        writer.flush();
    }

    /**
     * Prints differences in methods and fields of provided classes into provided <code>Writer</code>.
     * @param someClass first class
     * @param otherClass second class
     * @param writer <code>Writer</code> to write to
     * @throws IOException if exception happens while writing
     */
    public static void printDifference(Class<?> someClass, Class<?> otherClass, Writer writer) throws IOException {
        ClassDifferencePrinter.writeClassesDifference(someClass, otherClass, writer);
    }
}
