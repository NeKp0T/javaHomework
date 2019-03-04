package com.example.reflector;

import java.io.*;
import java.lang.reflect.*;
import java.util.List;


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

        private void writeModifiers(int modifiers) throws IOException {
            writeTabs();
            if ((modifiers & Modifier.PUBLIC) != 0) {
                writer.write("public ");
            }
            if ((modifiers & Modifier.PROTECTED) != 0) {
                writer.write("protected ");
            }
            if ((modifiers & Modifier.PRIVATE) != 0) {
                writer.write("private ");
            }
            if ((modifiers & Modifier.ABSTRACT) != 0) {
                writer.write("abstract ");
            }
            if ((modifiers & Modifier.STATIC) != 0) {
                writer.write("static ");
            }
            if ((modifiers & Modifier.FINAL) != 0) {
                writer.write("final ");
            }
            if ((modifiers & Modifier.TRANSIENT) != 0) {
                writer.write("transient ");
            }
            if ((modifiers & Modifier.VOLATILE) != 0) {
                writer.write("volatile ");
            }
            if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
                writer.write("synchronised ");
            }
            if ((modifiers & Modifier.NATIVE) != 0) {
                writer.write("native ");
            }
            if ((modifiers & Modifier.STRICT) != 0) {
                writer.write("strictfp ");
            }
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
            writeLn(printedClass.toGenericString()
                    .replace(printedClass.getName(),
                             printedClass.getSimpleName())
                    + " {");
        }

        private void writeType(Type type) throws IOException {
            writer.write(type.getTypeName().replaceAll("\\$", "."));
        }

        private void writeFields() throws IOException {
            Field[] fields = printedClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic()) {
                    continue;
                }
                writeModifiers(field.getModifiers());
                writeType(field.getGenericType());
                writer.append(" ");
                writer.write(field.getName());
                writer.write(";\n");
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

        private void writeArgumentsExceptionsAndBody(Executable executable) throws IOException {
            writer.write("(");
            Type[] argumentTypes = executable.getGenericParameterTypes();
//                TypeVariable<Method>[] typeParameters = executable.getTypeParameters();
            for (int i = 0; i < argumentTypes.length; i++) {
                writeType(argumentTypes[i]);
                writer.write(" arg" + i);
                if (i + 1 != argumentTypes.length) {
                    writer.write(", ");
                }
            }
            writer.write(") ");

            Type[] exceptionTypes = executable.getGenericExceptionTypes();
            if (exceptionTypes.length != 0) {
                writer.write("throws ");
                for (Type exceptionType : exceptionTypes) {
                    writeType(exceptionType);
                    writer.write(", ");
                }
            }

            writer.write(" {\n");
            tabCount++;
            writeLn("throw new UnsupportedOperationException();");
            tabCount--;
            writeLn("}\n");
        }

        private void writeConstructors() throws IOException {
            Constructor<?>[] constructors = printedClass.getDeclaredConstructors();
            for (Constructor<?> i : constructors) {
                writeModifiers(i.getModifiers());
                writer.write(printedClass.getSimpleName());
                writeArgumentsExceptionsAndBody(i);
            }
            writeLn("");
        }


        private void writeMethods() throws IOException { // TODO varargs?
            Method[] methods = printedClass.getDeclaredMethods();
            for (Method method : methods) {
                writeModifiers(method.getModifiers());
                writeType(method.getGenericReturnType());
                writer.append(" ");
                writeArgumentsExceptionsAndBody(method);
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
        int I;
        String sss;
        Kek<Kek<String>> rec;

        com.example.reflector.Reflector.Kek Kek() {
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
