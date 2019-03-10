package com.example.reflector;

import java.io.*;
import java.util.List;


// TODO SecurityException
// TODO deal with arrays, interfaces, etc
// TODO docs
public class Reflector {
    public static void printStructure(Class<?> someClass) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(someClass.getName() + ".java"))) {
            printStructure(someClass, writer);
        }
    }

    public static void printStructureToConsole(Class<?> someClass) throws IOException {
        var writer = new PrintWriter(System.out);
        printStructure(someClass, writer);
        writer.flush();
    }

    public static void printDifferenceToConsole(Class<?> someClass, Class<?> otherClass) throws IOException {
        var writer = new PrintWriter(System.out);
        printDifference(someClass, otherClass, writer);
        writer.flush();
    }

    public static void printStructure(Class<?> someClass, Writer writer) throws IOException {
        ClassStructurePrinter.writeClass(someClass, writer);
    }

    public static void printDifference(Class<?> someClass, Class<?> otherClass, Writer writer) throws IOException {
        ClassDifferencePrinter.writeClassesDifference(someClass, otherClass, writer);
    }

    // ---------------------------------------------

    public static void main(String[] args) {
        try {
            printStructureToConsole(Kek.class);
//            printDifferenceToConsole(ClassWithFields.class, DifferentClassWithFields.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class Kek<T> {
        T bob;
        T Kek;
        int I;
        String sss;
        Kek<Kek<String>> rec;

        Reflector.Kek Kek() {
            return null;
        }
        <Y> void keklol(Y y, T t) {

        }
        <Z> Z ww(Z z) {
            return z;
        }
        Kek<Integer> oof() { return null; }
        Reflector nw() { return null; }

        String constant() { return "bob!"; }

        void bounds(List<? extends String> l1, List<? super String> l2) {}

        private Kek() {}
    }
}
