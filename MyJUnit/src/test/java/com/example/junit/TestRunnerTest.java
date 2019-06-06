package com.example.junit;

import com.example.junit.logic.TestRunner;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.Mockito.*;


@SuppressWarnings("SpellCheckingInspection")
class TestRunnerTest {

    @Test
    void simpleTest() throws IOException {
        test("SimpleTest", new String[]{"Ok: Test public void com.example.junit.testclasses.SimpleTest.testMethod() passed"});
    }

    @Test
    void beforeClass() throws IOException {
        test("BeforeClassTest", new String[]{"" +
                "Ok: Test public void com.example.junit.testclasses.BeforeClassTest.test() passed"
        });
    }

    @Test
    void afterClass() throws IOException {
        test("AfterClassTest", new String[]{
                "Ok: Test public void com.example.junit.testclasses.AfterClassTest.test() passed"
        });
    }

    @Test
    void beforeAndAfter() throws IOException {
        test("BeforeAfterTest", new String[]{
                "Ok: Test public void com.example.junit.testclasses.BeforeAfterTest.test1() passed",
                "Ok: Test public void com.example.junit.testclasses.BeforeAfterTest.test2() passed"
        });
    }

    @Test
    void ignore() throws IOException {
        test("IgnoreTest", new String[]{
                "Ok: Method void com.example.junit.testclasses.IgnoreTest.test() ignored, cause = fortest"
        });
    }

    @Test
    void throwsCorrect() throws IOException {
        test("ThrowCorrectTest", new String[]{
                "Ok: Method public void com.example.junit.testclasses.ThrowCorrectTest.test() has thrown java.lang.NullPointerException as expected"
        });
    }

    @Test
    void throwsWrong() throws IOException {
        var expected = new String[2];
        expected[0] = "Error: Method public void com.example.junit.testclasses.ThrowWrongTest.test() has thrown unexpected exception class java.lang.NullPointerException:";
        expected[1] = "com.example.junit.testclasses.ThrowWrongTest.test(ThrowWrongTest.java:10)"; // TODO
        test("ThrowWrongTest", expected);
    }

    private void test(String className, String[] output) throws IOException {
        File dir = Files.createTempDirectory("testMyJunit").toFile();
        compileClass(Path.of("com", "example", "junit", "testclasses", className + ".java"), dir);

        PrintStream mock = mock(PrintStream.class);

        new TestRunner().test(dir, mock);

        InOrder inOrder = inOrder(mock);
        for (String s : output) {
            inOrder.verify(mock).println(ArgumentMatchers.eq(s));
        }

        int timesRunningTimeCounted = (int) Arrays.stream(output)
                .filter(s -> (s.contains("Ok") || s.contains("Error")) && (!s.contains("ignored")))
                .count();

        verify(mock, times(output.length + timesRunningTimeCounted))
                .println(ArgumentMatchers.anyString());

        dir.delete();
    }

    private void compileClass(Path filename, File outputDirectory) throws IOException {
        Path sourceFilePath = outputDirectory.toPath().resolve(filename);
        sourceFilePath.toFile().getParentFile().mkdirs();
        Files.copy(Path.of(".", "src", "test", "java").resolve(filename), sourceFilePath);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFilePath.toString());
    }
}