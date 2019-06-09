package com.example.junit.logic;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows to logs various events and copy logs into PrintStream.
 * All log method are synchronized.
 */
class TestLogger {
    private static final String ERROR = "Error: ";
    private static final String OK = "Ok: ";
    private static final String WARNING = "Warning: ";

    private final List<String> logs = new LinkedList<>();
    private final Object lock = new Object();

    public void logNoSuitableConstructor(Class<?> clazz) {
        logError(clazz.getName() + " doesn't have a public constructor without arguments");
    }

    public void logInstantiationError(Class<?> clazz, InstantiationError e) {
        synchronized (lock) {
            logError("Instantiation error for class " + clazz);
            logException(e);
        }
    }

    public void logConstructorError(Class<?> clazz, Throwable e) {
        synchronized (lock) {
            logError("Unknown error while constructing class " + clazz);
            logException(e);
        }
    }

    public void logIllegalAccess(Method method, Class<? extends Annotation> annotation) {
        synchronized (lock) {
            logError("Access to method " + method + " annotated with " + annotation + " denied");
            logRemove(method);
        }
    }

    public void logInaccessibleConstructor(Class<?> clazz) {
        logError("Access to constructor of class " + clazz + " denied");
    }

    public void logMethodRequiresArguments(Method method, Class<? extends Annotation> annotation) {
        synchronized (lock) {
            logError("Method " + method + " annotated with " + annotation + " requires arguments");
            logRemove(method);
        }
    }

    private void logRemove(Method method) {
        log(" ".repeat(ERROR.length()) + "not calling " + method);
    }

    public void logIgnored(Method testMethod, String ignoreCause) {
        logOk("Method " + testMethod + " ignored, cause = " + ignoreCause);
    }

    public void logExpectedThrow(Method method, Throwable e) {
        logOk("Method " + method + " has thrown " + e + " as expected");
    }

    public void logUnexpectedThrow(Method method, Throwable e) {
        synchronized (lock) {
            logError("Method " + method + " has thrown unexpected exception " + e.getClass() + ":");
            logException(e);
        }
    }

    public void logConstructorThrows(Class<?> clazz, Throwable e) {
        synchronized (lock) {
            logError("Constructor for class " + clazz + ":");
            logException(e);
        }
    }

    public void logOk(Method testMethod, Duration between) {
        synchronized (lock) {
            logOk("Test " + testMethod + " passed");
            logTime(between);
        }
    }

    public void logFail(Method testMethod, Duration between) {
        synchronized (lock) {
            logError("Test " + testMethod + " failed");
            logTime(between);
        }
    }

    public void logTime(Duration between) {
        log("    finished in " + between.toMillis() + " ms");
    }

    public void logReturns(Method method, Class<? extends Annotation> annotation) {
        logWarning("Method " + method + " anotated with " + annotation + " returns");
    }

    /**
     * Prints all logs into provided {@link PrintStream}
     * This method is not synchronized!
     */
    public void writeToOutput(PrintStream output) {
        for (String s : logs) {
            output.println(s);
        }
    }

    private void logException(Throwable e) {
        log(e.getStackTrace()[0].toString());
    }

    private void logError(String s) {
        log(ERROR + s);
    }

    private void logOk(String s) {
        log(OK + s);
    }

    private void logWarning(String s) {
        log(WARNING + s);
    }

    private void log(String s) {
        synchronized (lock) {
            logs.add(s);
        }
    }

    public void logNotStatic(Method method, Class<? extends Annotation> annotation) {
        synchronized (lock) {
            logError("Method " + method + " annotated with " + annotation + " is not static");
            logRemove(method);
        }
    }

    public void logStatic(Method method, Class<? extends Annotation> annotation) {
        logWarning("Method " + method + " annotated with " + annotation + " is static");
    }
}
