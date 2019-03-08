package com.example.reflector;

import java.io.*;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
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
        ReflectorPrinter.printStructure(someClass, writer, 0);
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
            if (Modifier.isPublic(modifiers)) {
                writer.write("public ");
            }
            if (Modifier.isProtected(modifiers)) {
                writer.write("protected ");
            }
            if (Modifier.isPrivate(modifiers)) {
                writer.write("private ");
            }
            if (Modifier.isAbstract(modifiers)) {
                writer.write("abstract ");
            }
            if (Modifier.isStatic(modifiers)) {
                writer.write("static ");
            }
            if (Modifier.isFinal(modifiers)) {
                writer.write("final ");
            }
            if (Modifier.isTransient(modifiers)) {
                writer.write("transient ");
            }
            if (Modifier.isVolatile(modifiers)) {
                writer.write("volatile ");
            }
            if (Modifier.isSynchronized(modifiers)) {
                writer.write("synchronised ");
            }
            if (Modifier.isNative(modifiers)) {
                writer.write("native ");
            }
            if (Modifier.isStrict(modifiers)) {
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
            writeLn(printedClass.toGenericString() // TODO rewrite without cheating
                    .replace(printedClass.getName(),
                             printedClass.getSimpleName())
                    + " {");
        }

        private void writeType(Type type) throws IOException {
            writer.write(type.getTypeName().replaceAll("\\$", "."));
        }

        private void writeFields() throws IOException {
            Field[] fields = printedClass.getDeclaredFields();
            sortMembers(fields);
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

        private void writeArguments(Executable executable) throws IOException {
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

        }

        private void writeExceptions(Executable executable) throws IOException {
            Type[] exceptionTypes = executable.getGenericExceptionTypes();
            if (exceptionTypes.length != 0) {
                writer.write("throws ");
                for (int i = 0; i < exceptionTypes.length; i++) {
                    Type exceptionType = exceptionTypes[i];
                    writeType(exceptionType);
                    if (i + 1 < exceptionTypes.length) {
                        writer.write(", ");
                    }
                }
            }
        }

        private void writeDefaultFunctionBody() throws IOException {
            writer.write("{\n");
            tabCount++;
            writeLn("throw new UnsupportedOperationException();");
            tabCount--;
            writeLn("}\n");
        }

        private void writeArgumentsExceptionsAndBody(Executable executable) throws IOException {
            writeArguments(executable);
            writeExceptions(executable);
            writeDefaultFunctionBody();
        }

        private void writeConstructors() throws IOException {
            Constructor<?>[] constructors = printedClass.getDeclaredConstructors();
            sortMembers(constructors);
            for (Constructor<?> i : constructors) {
                writeModifiers(i.getModifiers());
                writer.write(printedClass.getSimpleName());
                writeArgumentsExceptionsAndBody(i);
            }
            writeLn("");
        }

        private void writeMethods() throws IOException { // TODO varargs?
            Method[] methods = printedClass.getDeclaredMethods();
            sortMembers(methods);
            for (Method method : methods) {
                writeModifiers(method.getModifiers());
                writeType(method.getGenericReturnType());
                writer.append(" ");
                writer.write(method.getName());
                writeArgumentsExceptionsAndBody(method);
            }
            writeLn("");
        }
    }

    private static void sortMembers(Member[] fields) {
        Arrays.sort(fields, Comparator.comparing(Member::getName));
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
