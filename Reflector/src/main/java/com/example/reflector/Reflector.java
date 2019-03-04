package com.example.reflector;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


// TODO SecurityException
// TODO deal with arrays, intefaces, etc
public class Reflector {
    public static void printStructure(Class<?> someClass) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(someClass.getName() + ".java"))) {
            ReflectorPrinter.printStructure(someClass, writer, 0);
        }
    }

    public static void actuallyPrintStructure(Class<?> someClass) throws IOException {
        try (var writer = new PrintWriter(System.out)) {
            ReflectorPrinter.printStructure(someClass, writer, 0);
        }
    }

    private static class ReflectorPrinter {
        Class<?> printedClass;
        Writer writer;
        int tabCount;

        public static void printStructure(Class<?> someClass, Writer writer, int tabCount) throws IOException {
            new ReflectorPrinter(someClass, writer, tabCount).writeStructure();

        }

        private ReflectorPrinter(Class<?> someClass, Writer writer, int tabCount) {
            this.writer = writer;
            this.printedClass = someClass;
            this.tabCount = tabCount;
        }

        private void writeTabs() throws IOException {
            for (int i = 0; i < tabCount; i++) {
                writer.append('\t');
            }
        }

        private void writeLn(String string) throws IOException {
            writeTabs();
            writer.write(string + "\n");
        }

        private void writeStructure() throws IOException {
            writeClassNameLine();
            tabCount++;

            writeFields();
            writeMethods();
            writeConstructors();
            writeSubclasses();

            tabCount--;
            writeLn("}");
        }

        private void writeClassNameLine() throws IOException {
            writeLn(printedClass.getName());
            writeLn(printedClass.getCanonicalName());
            writeLn(printedClass.getPackageName());
            writeLn(printedClass.getSimpleName());
            writeLn(printedClass.getTypeName());
            writeLn(printedClass.toString());
            writeLn(printedClass.toGenericString()
                    .replace(printedClass.getName(),
                             printedClass.getSimpleName())
                    + " {");
        }

        private void writeFields() throws IOException {
            Field[] fields = printedClass.getDeclaredFields();
            for (Field i : fields) {
                writeLn(i.toGenericString()
                        .replaceAll("\\$",
                                ".")
                        + ";");
            }
            writeLn("");
        }

        private void writeSubclasses() throws IOException {
            Class<?>[] subclasses = printedClass.getDeclaredClasses();
            for (Class<?> i : subclasses) {
                printStructure(i, writer, tabCount);
            }
            writeLn("");
        }


        private void writeConstructors() throws IOException {
            Constructor<?>[] constructors = printedClass.getDeclaredConstructors();
            for (Constructor<?> i : constructors) {
                writeLn(i.toGenericString()
                        .replaceAll("\\$",
                                ".")
                        + " {");
                writeLn("}");
            }
            writeLn("");
        }


        private void writeMethods() throws IOException {
            Method[] methods = printedClass.getDeclaredMethods();
            for (Method i : methods) {
                writeLn(i.toGenericString()
                        .replaceAll("\\$",
                                ".")
                        + " {");
                writeLn("\tthrow new NotImplementedException();"); // TODO write smth else?
                writeLn("}");
            }
            writeLn("");
        }
    }

    public static void main(String[] args) {
        try {
            actuallyPrintStructure(Reflector.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private class Kek<T> {
        T bob;
        T Kek;

        com.example.reflector.Reflector.Kek Kek() {
            return null;
        }
        <Y> void keklol(Y y, T t) {

        }
        Reflector nw() { return null; }

        private Kek() {}
    }
}
