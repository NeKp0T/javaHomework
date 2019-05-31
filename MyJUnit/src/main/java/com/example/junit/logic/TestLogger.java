package com.example.junit.logic;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows to logs various events and copy logs into PrintStream
 */
class TestLogger {
    private static final String ERROR = "Error: ";
    private static final String OK = "Ok: ";
    private final List<String> logs = new LinkedList<>();

    public void logNoSuitableConstructor(Class<?> clazz) {
        log(ERROR + clazz.getName() + " doesn't have a public constructor without arguments");
    }

    public void logIllegalAccess(Method method, Class<? extends Annotation> annotation) {
        log(ERROR + "Access to method " + method + " annotated with " + annotation + " denied");
    }

    public void logMethodRequiresArguments(Method method, Class<? extends Annotation> annotation) {
        log(ERROR + "Method " + method + " annotated with " + annotation + " requires arguments");
    }

    public void logIgnored(Method testMethod, String ignoreCause) {
        log(OK + "Method " + testMethod + " ignored, cause = " + ignoreCause);
    }

    public void logExpectedThrow(Method method, Throwable e) {
        log(OK + "Method " + method + " has thrown " + e + " as expected");
    }

    public void logUnexpectedThrow(Method method, Throwable e) {
        log(ERROR + "Method " + method + " has thrown unexpected exception " + e.getClass() + ":");
        logException(e);
    }

    public void logConstructorThrows(Class<?> clazz, Throwable e) {
        log(ERROR + "Constructor for class " + clazz + ":");
        logException(e);
    }

    public void logOk(Method testMethod) {
        log(OK + "Test " + testMethod + " passed");
    }

    public void logTime(Method testMethod, Duration between) {
        log("" + testMethod + " finished in " + between.toMillis() + " ms");
    }

    /**
     * Prints all logs into provided {@link PrintStream}
     */
    public void writeToOutput(PrintStream output) {
        for (String s : logs) {
            output.println(s);
        }
    }

    private void logException(Throwable e) {
        log(e.getStackTrace()[0].toString());
    }

    private void log(String s) {
        logs.add(s);
    }
}
