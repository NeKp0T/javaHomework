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
        try (var writer = new PrintWriter(System.out)) {
            printStructure(someClass, writer);
        }
    }

    public static void printStructure(Class<?> someClass, Writer writer) throws IOException {
        ClassStructurePrinter.writeClass(someClass, writer);
    }
    // ---------------------------------------------

    public static void main(String[] args) {
        try {
            printStructureToConsole(Kek.class);
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
