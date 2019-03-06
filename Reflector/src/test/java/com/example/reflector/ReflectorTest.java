package com.example.reflector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClassWithFields {
    int intField;
    java.lang.String stringField;
    java.lang.String[] stringArr;
    char[] charArr;
}

class ClassWithMethodsWithoutArguments {
    void voidFunc() {
        throw new UnsupportedOperationException();
    }
    String stringFunc() {
        throw new UnsupportedOperationException();
    }
    ClassWithMethodsWithoutArguments() {
        throw new UnsupportedOperationException();
    }
}

class ClassWithMethodsWithArguments {
    void voidFuncFromInt(int arg0) {
        throw new UnsupportedOperationException();
    }
    java.lang.String stringFuncFromString(java.lang.String arg0) {
        throw new UnsupportedOperationException();
    }
    java.lang.Integer multipleArguments(java.lang.String arg0, java.lang.Integer arg1, char arg2) {
        throw new UnsupportedOperationException();
    }
    ClassWithMethodsWithArguments() {
        throw new UnsupportedOperationException();
    }
    ClassWithMethodsWithArguments(int arg0, char arg1, java.lang.String arg2) {
        throw new UnsupportedOperationException();
    }
}

public class ReflectorTest {

    StringWriter writer;

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

        for (int i = 0; i < nonBlankLines1.length; i++) {
            if (!nonBlankLines1[i].equals(nonBlankLines2[i])) {
                System.out.println("-" + nonBlankLines1[i]);
                System.out.println("+" + nonBlankLines2[i]); // TODO remove?
            }
        }
        return Arrays.equals(nonBlankLines1, nonBlankLines2);
    }

    private void assertPrintsCorrectly(String correct, Class<?> testedClass) throws IOException {
        Reflector.printStructure(testedClass, writer);
        assertTrue(compareSkippingEmptyLines(correct, writer.toString()));
    }

    @Test
    void printFieldsTest() throws IOException {
        String correct = "class ClassWithFields {\n" +
                "\tint intField;\n" +
                "\tjava.lang.String stringField;\n" +
                "\tjava.lang.String[] stringArr;\n" +
                "\tchar[] charArr;\n" +
                "\tClassWithFields() {\n" +
                "\t\tthrow new UnsupportedOperationException();\n" +
                "\t}\n" +
                "}";
        assertPrintsCorrectly(correct, ClassWithFields.class);
    }

    @Test
    void printMethodsWithoutArguments() throws IOException {
        String correct = "class ClassWithMethodsWithoutArguments {\n" +
                "    void voidFunc() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    java.lang.String stringFunc() {\n" +
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
                "    void voidFuncFromInt(int arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    java.lang.String stringFuncFromString(java.lang.String arg0) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    java.lang.Integer multipleArguments(java.lang.String arg0, java.lang.Integer arg1, char arg2) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    ClassWithMethodsWithArguments() {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "    ClassWithMethodsWithArguments(int arg0, char arg1, java.lang.String arg2) {\n" +
                "        throw new UnsupportedOperationException();\n" +
                "    }\n" +
                "}"; // TODO make them have one thing so order wouldnt matter
        assertPrintsCorrectly(correct, ClassWithMethodsWithArguments.class);
    }
}
