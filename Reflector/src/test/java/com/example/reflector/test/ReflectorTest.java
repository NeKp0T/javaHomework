package com.example.reflector.test;

import com.example.reflector.Reflector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

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
                System.out.println("+" + nonBlankLines2[i]); // TODO remove?
            }
        }
        return Arrays.equals(nonBlankLines1, nonBlankLines2);
    }

    // TODO rename testedClass
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
                "}"; // TODO
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

//  Reflector.printDifferenceToConsole(1.class, 2.class);
}
