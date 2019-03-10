package com.example.reflector.test;

import com.example.reflector.Reflector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectorTest {

    private StringWriter writer;

    @BeforeEach
    void initWriter() {
        writer = new StringWriter();
    }

    private boolean compareSkippingEmptyLines(String expected, String actual) {
        var nonBlankLines1 = expected.lines()
                .filter(s -> !s.isBlank())
                .map(s -> s.replaceAll("    ", "\t"))
                .toArray();
        var nonBlankLines2 = actual.lines().filter(s -> !s.isBlank()).toArray();

        for (int i = 0; i < nonBlankLines1.length && i < nonBlankLines2.length; i++) {
            if (!nonBlankLines1[i].equals(nonBlankLines2[i])) {
                System.out.println("-" + nonBlankLines1[i]);
                System.out.println("+" + nonBlankLines2[i]);
            }
        }
        return Arrays.equals(nonBlankLines1, nonBlankLines2);
    }

    private void assertPrintsCorrectly(String correct, Class<?> testedClass) throws IOException {
        Reflector.printStructure(testedClass, writer);
        assertTrue(compareSkippingEmptyLines(correct, writer.toString()));
    }

    private void assertPrintsDifferenceCorrectly(String correct, Class<?> testedClass, Class<?> otherClass) throws IOException {
        Reflector.printDifference(testedClass, otherClass, writer);
        assertTrue(compareSkippingEmptyLines(correct, writer.toString()));
    }

    @Test
    void printFieldsTest() throws IOException {
        String correct = "class ClassWithFields {\n" +
                "\tchar[] charArr;\n" +
                "\tint intField;\n" +
                "\tjava.lang.String[] stringArr;\n" +
                "\tjava.lang.String stringField;\n" +
                "\tClassWithFields() {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithFields.class);
    }

    @Test
    void printMethodsWithoutArguments() throws IOException {
        String correct = "class ClassWithMethodsWithoutArguments {\n" +
                "    java.lang.String stringFunc() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    void voidFunc() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    ClassWithMethodsWithoutArguments() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithMethodsWithoutArguments.class);
    }

    @Test
    void printMethodsWithArguments() throws IOException {
        String correct = "class ClassWithMethodsWithArguments {\n" +
                "    java.lang.Integer multipleArguments(java.lang.String arg0, java.lang.Integer arg1, char arg2) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    java.lang.String stringFuncFromString(java.lang.String arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    void voidFuncFromInt(int arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    ClassWithMethodsWithArguments() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    ClassWithMethodsWithArguments(int arg0, char arg1, java.lang.String arg2) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithMethodsWithArguments.class);
    }

    @Test
    void printGenericMethods() throws IOException {
        String correct = "public class ClassWithGenericMethods {\n" +
                "    <T> T aMethod(T arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    <W> W bMethod(W[] arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    <U> U cMethod(java.util.Set<? extends U> arg0, java.util.Set<? super U> arg1) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    <X, Y> void dMethod(X[] arg0, Y arg1) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    <Z> ClassWithGenericMethods(Z arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "}\n";
        assertPrintsCorrectly(correct, ClassWithGenericMethods.class);
    }

    @Test
    void printClassWithSuperclass() throws IOException {
        String correct = "public class ClassWithSuperclass1 extends com.example.reflector.test.ClassSuperclass1 {\n" +
                "\tpublic ClassWithSuperclass1() {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithSuperclass1.class);
    }

    @Test
    void printInnerNestedClasses() throws IOException {
        String correct = "public class ClassWithInnerAndNestedClasses {\n" +
                "\tpublic ClassWithInnerAndNestedClasses() {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\tpublic class NestedClass {\n" +
                "\t\tpublic NestedClass(com.example.reflector.test.ClassWithInnerAndNestedClasses arg0) {\n" +
                "\t\t\tthrow new UnsupportedOperationException();\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tstatic class InnerClass {\n" +
                "\t\tInnerClass() {\n" +
                "\t\t\tthrow new UnsupportedOperationException();\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        assertPrintsCorrectly(correct, ClassWithInnerAndNestedClasses.class);
    }

    @Test
    void printFieldsDifference() throws IOException {
        String correct = "<class ClassWithFields {\n" +
                ">class DifferentClassWithFields {\n" +
                "<\tint intField;\n" +
                "\n" +
                "<\tjava.lang.String[] stringArr;\n" +
                "\n" +
                "<|\tjava.lang.String stringField;\n" +
                ">|\tjava.lang.Integer stringField;\n" +
                "\n" +
                ">\tint differentIntField;\n" +
                "\n" +
                ">\tlong newField;\n" +
                "\n" +
                "}";
        assertPrintsDifferenceCorrectly(correct, ClassWithFields.class, DifferentClassWithFields.class);
    }

    @Test
    void printMethodsDifference() throws IOException {
        String correct = "<public class ClassMethodDifference1 {\n" +
                ">public class ClassMethodDifference2 {\n" +
                "<|\tvoid changesArgumentCount(int arg0, int arg1) \n" +
                ">|\tvoid changesArgumentCount(int arg0, int arg1, int arg2) \n" +
                "\n" +
                "<|\tvoid changesArgumentType(int arg0) \n" +
                ">|\tvoid changesArgumentType(long arg0) \n" +
                "\n" +
                "<|\tjava.lang.String changesReturnType() \n" +
                ">|\tjava.lang.Integer changesReturnType() \n" +
                "\n" +
                "<|\tpublic void changesVisibility() \n" +
                ">|\tprivate void changesVisibility() \n" +
                "\n" +
                "<\tvoid presentOnlyInFirst() \n" +
                "\n" +
                ">\tvoid presentOnlyInSecond() \n" +
                "\n" +
                "}";
        assertPrintsDifferenceCorrectly(correct, ClassMethodDifference1.class, ClassMethodDifference2.class);
    }

    @Test
    void printMethodsDifferenceWithMultipleMethodInstances() throws IOException {
        String correct = "<public class ClassMultipleMethodsWithSameNameDifference1 {\n" +
                ">public class ClassMultipleMethodsWithSameNameDifference2 {\n" +
                "<|\tpublic void method(int arg0, int arg1) \n" +
                "<|\tint method(int arg0) \n" +
                "<|\tvoid method() \n" +
                ">|\tprivate void method(int arg0, int arg1) \n" +
                ">|\tlong method(int arg0) \n" +
                ">|\tjava.lang.String method(int arg0, int arg1, int arg2, int arg3) \n" +
                "}";
        assertPrintsDifferenceCorrectly(correct, ClassMultipleMethodsWithSameNameDifference1.class, ClassMultipleMethodsWithSameNameDifference2.class);
    }

    @Test
    void printsGenericClassParametersCorrectly() throws IOException {
        String correct = "public class ClassWithGenerics1<U, V extends java.awt.print.Printable & java.lang.Iterable<java.lang.Integer>, X extends U> {\n" +
                "\tU fieldA;\n" +
                "\t\n" +
                "\tvoid f(U arg0, V arg1) {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\n" +
                "\t<T> T genericMethodA(T arg0) {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\n" +
                "\t<T> T genericMethodB(T arg0) {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\n" +
                "\tvoid genericMethodC(java.util.Set<?> arg0) {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\n" +
                "\tvoid genericMethodD(java.util.Set<? extends java.lang.String> arg0) {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "\n" +
                "\t\n" +
                "\tpublic ClassWithGenerics1() {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithGenerics1.class);
    }

    @Test
    void genericAndWildcardDifference() throws IOException {

        String correct = "<public class ClassWithGenerics1<U, V extends java.awt.print.Printable & java.lang.Iterable<java.lang.Integer>, X extends U> {\n" +
                ">public class ClassWithGenerics2<U, V extends U> {\n" +
                "<|\tU fieldA;\n" +
                ">|\tU fieldA;\n" +
                "\n" +
                "<\tvoid f(U arg0, V arg1) \n" +
                "\n" +
                "<|\tT genericMethodA(T arg0) \n" +
                ">|\tT genericMethodA(T arg0) \n" +
                "\n" +
                "<|\tT genericMethodB(T arg0) \n" +
                ">|\tW genericMethodB(W arg0) \n" +
                "\n" +
                "<|\tvoid genericMethodD(java.util.Set<? extends java.lang.String> arg0) \n" +
                ">|\tvoid genericMethodD(java.util.Set<?> arg0) \n" +
                "\n" +
                "}";
        assertPrintsDifferenceCorrectly(correct, ClassWithGenerics1.class, ClassWithGenerics2.class);
    }

    @Test
    void printDiffOfClassesWithSuperclasses() throws IOException {
        StringWriter writerSuper = new StringWriter();
        StringWriter writerExtends = new StringWriter();
        Reflector.printDifference(ClassSuperclass1.class, ClassSuperclass2.class, writerSuper);
        Reflector.printDifference(ClassWithSuperclass1.class, ClassWithSuperclass2.class, writerExtends);

        String superDifference = writerSuper.toString();
        String extendsDifference = writerExtends.toString();
        Stream<String> s1 = superDifference.lines().skip(2);
        Stream<String> s2 = extendsDifference.lines().skip(2);

        Iterator<String> iterator1 = s1.iterator(), iterator2 = s2.iterator();
        while(iterator1.hasNext() && iterator2.hasNext())
            assertEquals(iterator1.next(), iterator2.next());
        assert !iterator1.hasNext() && !iterator2.hasNext();
    }

    @Test
    void printCompileAndDiffTests() throws IOException, ClassNotFoundException {
        printCompileAndDiff(ClassWithFields.class);
        printCompileAndDiff(ClassWithMethodsWithArguments.class);
        printCompileAndDiff(ClassWithGenericMethods.class);
        printCompileAndDiff(ClassMultipleMethodsWithSameNameDifference1.class);
        printCompileAndDiff(ClassSuperclass1.class);
    }

    void printCompileAndDiff(Class classToTest) throws IOException, ClassNotFoundException {
        String className = classToTest.getSimpleName();
        File sourceFile   = new File("/tmp/" + className + ".java");
        try (FileWriter writer = new FileWriter(sourceFile)) {
            Reflector.printStructure(classToTest, writer);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Collections.singletonList(new File("/tmp")));

        compiler.getTask(null,
                fileManager,
                null,
                null,
                null,
                fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile)))
                .call();
        fileManager.close();

        ClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:///tmp/")});
        Class loadedClass = classLoader.loadClass(className);

        var compareToLoadedWriter = new StringWriter();
        var compareToItselfWriter = new StringWriter();

        Reflector.printDifference(classToTest, loadedClass, compareToLoadedWriter);
        Reflector.printDifference(classToTest, loadedClass, compareToItselfWriter);

        assertEquals(compareToItselfWriter.toString(), compareToLoadedWriter.toString());

        sourceFile.deleteOnExit();
        File binaryFile   = new File("/tmp/" + className + ".class");
        binaryFile.delete();
    }

}
