package com.example.junit.logic;

import com.example.junit.annotations.*;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A task that tests a whole class
 */
class TestClassTask implements Runnable {
    private final Class<?> testClass;
    private final TestLogger logger;
    private final ExecutorService executor;
    private CountDownLatch executedLatch;

    /**
     * Constructs a task to test provided class with provided logger.
     * @param classToTest class to test
     * @param logger      logger to log tests output to
     * @param executor    executor to run tests in
     */
    public TestClassTask(Class<?> classToTest, TestLogger logger, ExecutorService executor, CountDownLatch executedLatch) {
        this.testClass = classToTest;
        this.logger = logger;
        this.executor = executor;
        this.executedLatch = executedLatch;
    }

    /**
     * Starts testing
     */
    @Override
    public void run() {
        if (needsLaunch(testClass)) {
            runClass();
        } else {
            executedLatch.countDown();
        }
    }

    /**
     * Test whether class has any methods to run
     * @param clazz class to test
     * @return whether class has any methods to run
     */
    private static boolean needsLaunch(Class<?> clazz) {
        var annotationClasses = new ArrayList<Class<? extends Annotation>>();
        annotationClasses.add(Test.class);
        annotationClasses.add(BeforeClass.class);
        annotationClasses.add(AfterClass.class);
        for (Method method : clazz.getDeclaredMethods()) {
            for (Class<? extends Annotation> classToLook : annotationClasses) {
                if (method.getAnnotation(classToLook) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tests class
     */
    private void runClass() {
        List<Method> beforeClass;
        List<Method> afterClass;
        List<Method> before;
        List<Method> after;
        List<Method> test;
        Object firstInstance;
        Object[] instances;

        boolean exiting = true;
        try {
            beforeClass = getAnnotatedMethods(BeforeClass.class);
            afterClass = getAnnotatedMethods(AfterClass.class);
            before = getAnnotatedMethods(Before.class);
            after = getAnnotatedMethods(After.class);
            test = getAnnotatedMethods(Test.class);

            Constructor testClassConstructor = getConstructor();
            if (testClassConstructor == null) {
                return;
            }

            firstInstance = createInstance(testClassConstructor);
            if (firstInstance == null) {
                return;
            }

            removeInvalid(beforeClass, BeforeClass.class, true, null);
            removeInvalid(afterClass, AfterClass.class, true, null);
            removeInvalid(before, Before.class, false, firstInstance);
            removeInvalid(after, After.class, false, firstInstance);
            removeInvalid(test, Test.class, false, firstInstance);

            instances = generateInstances(test.size() - 1, testClassConstructor);
            if (instances == null) {
                return;
            }

            tryToInvokeAll(null, beforeClass, BeforeClass.class);

            exiting = false;
        } finally {
            if (exiting) {
                executedLatch.countDown();
            }
        }

        var afterClassTask = new AfterClassTask(afterClass, test.size());

        for (int i = 0; i < test.size(); i++) {
            Object instance = (i == 0) ? firstInstance : instances[i - 1];
            executor.submit(new TestMethodTask(before, after, test.get(i), afterClassTask, instance));
        }
    }

    /**
     * Retrieves a constructor for <code>testClass</code>
     * @return proper constructor for <code>testClass</code> or null if there isn't one
     */
    @Nullable
    private Constructor getConstructor() {
        Constructor testClassConstructor;
        try {
            testClassConstructor = testClass.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.logNoSuitableConstructor(testClass);
            return null;
        }

        if (!testClassConstructor.canAccess(null)) {
            logger.logInaccessibleConstructor(testClass);
            return null;
        }
        return testClassConstructor;
    }

    /**
     * Generates required amount of <code>testClass</code> instances,
     * or reports an error while generating
     * @param n number of instances to generate
     * @return array of <code>n</code> instances or <code>null</code> if any error happens while generating
     */
    @Nullable
    private Object[] generateInstances(int n, Constructor testClassConstructor) {
        if (n < 0) {
            return null;
        }
        var instances = new Object[n];
        for (int i = 0; i < n; i++) {
            instances[i] = createInstance(testClassConstructor);
            if (instances[i] == null) {
                return null;
            }
        }

        return instances;
    }

    /**
     * Constructs a new object using provided constructor
     * @param testClassConstructor constructor to use
     * @return constructed instance or null if an error happened
     */
    @Nullable
    private Object createInstance(Constructor testClassConstructor) {
        try {
            return testClassConstructor.newInstance();
        } catch (InstantiationException e) {
            logger.logNoSuitableConstructor(testClass);
            return null;
        } catch (InvocationTargetException e) {
            logger.logConstructorThrows(testClass, e.getTargetException());
            return null;
        } catch (IllegalAccessException e) {
            // shouldn't happen since already checked access
            logger.logInaccessibleConstructor(testClass);
            return null;
        } catch (Throwable e) {
            logger.logConstructorError(testClass, e);
            return null;
        }
    }

    /**
     * Removes methods that cannot be ran an reports everything to logger
     * @param methods        list of methods to check
     * @param annotation     annotation to pass to logger
     * @param shouldBeStatic whether provided methods should be static
     * @param instance       instance to check accessibility of methods from
     */
    private void removeInvalid(List<Method> methods, Class<? extends Annotation> annotation, boolean shouldBeStatic, Object instance) {
        for (Iterator<Method> iterator = methods.iterator(); iterator.hasNext();) {
            Method method = iterator.next();
            if (method.getParameterCount() != 0) {
                logger.logMethodRequiresArguments(method, annotation);
                iterator.remove();
                continue;
            }
            if (shouldBeStatic && !Modifier.isStatic(method.getModifiers())) {
                logger.logNotStatic(method, annotation);
                iterator.remove();
                continue;
            }
            if (!method.canAccess(instance)) {
                logger.logIllegalAccess(method, annotation);
                iterator.remove();
                continue;
            }
            if (method.getReturnType() != void.class) {
                logger.logReturns(method, annotation);
            }
            if (!shouldBeStatic && Modifier.isStatic(method.getModifiers())) {
                logger.logStatic(method, annotation);
            }
        }
    }

    /**
     * Retrieves all methods of <code>testClass</code> annotated with provided annotation
     * @param annotationClass annotation to filter
     * @return list of all methods of <code>testClass</code> annotated with provided annotation
     */
    private List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(m -> m.getAnnotation(annotationClass) != null)
                .collect(Collectors.toList());
    }

    private void tryToInvoke(Object obj, Method method, Class<? extends Annotation> annotation) {
        try {
            method.invoke(obj);
        } catch (IllegalAccessException e) {
            logger.logIllegalAccess(method, annotation);
        } catch (IllegalArgumentException e) {
            logger.logMethodRequiresArguments(method, annotation);
        } catch (InvocationTargetException e) {
            Throwable thrown = e.getTargetException();
            logger.logUnexpectedThrow(method, thrown);
        }
    }

    private void tryToInvokeAll(Object obj, List<Method> methods, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            tryToInvoke(obj, method, annotation);
        }
    }

    /**
     * A task that should be executed after all test methods are done.
     */
    public class AfterClassTask {
        private final AtomicInteger testsLeft;
        private final List<Method> afterClass;

        public AfterClassTask(List<Method> afterClass, int testCount) {
            this.afterClass = afterClass;
            testsLeft = new AtomicInteger(testCount);
        }

        /**
         * This method is called in the end of every method test task.
         * It decrements number of left test methods and if reaches zero,
         * runs after class methods
         */
        public void finishTest() {
            if (testsLeft.decrementAndGet() == 0) {
                try {
                    tryToInvokeAll(null, afterClass, AfterClass.class);
                } finally {
                    executedLatch.countDown();
                }
            }
        }
    }

    /**
     * A task to run a test method
     */
    public class TestMethodTask implements Runnable {
        private final List<Method> before;
        private final List<Method> after;
        private final Method test;
        private final TestClassTask.AfterClassTask afterClass;
        private final Object instance;

        public TestMethodTask(List<Method> before, List<Method> after, Method test, AfterClassTask afterClass, Object instance) {
            this.before = before;
            this.after = after;
            this.test = test;
            this.afterClass = afterClass;
            this.instance = instance;
        }

        @Override
        public void run() {
            tryToInvokeAll(instance, before, Before.class);
            runTest();
            tryToInvokeAll(instance, after, After.class);
        }

        private void runTest() {
            try {
                Test testAnnotation = test.getAnnotation(Test.class);
                if (!testAnnotation.ignoreCause().equals(Test.DONT_IGNORE)) {
                    logger.logIgnored(test, testAnnotation.ignoreCause());
                    return;
                }

                Class<? extends Throwable> expectedException = testAnnotation.expected();

                boolean logOk = true;
                Instant timeBefore = Instant.now();
                Instant timeAfter = null;
                try {
                    test.invoke(instance);
                } catch (IllegalAccessException e) {
                    timeAfter = Instant.now();
                    logger.logIllegalAccess(test, Test.class);
                    logOk = false;
                } catch (IllegalArgumentException e) {
                    timeAfter = Instant.now();
                    logger.logMethodRequiresArguments(test, Test.class);
                    logOk = false;
                } catch (InvocationTargetException e) {
                    timeAfter = Instant.now();
                    Throwable thrown = e.getTargetException();
                    if (expectedException.isInstance(thrown)) {
                        logger.logExpectedThrow(test, thrown);
                    } else {
                        logOk = false;
                        logger.logUnexpectedThrow(test, thrown);
                    }
                }

                if (timeAfter == null) {
                    timeAfter = Instant.now();
                }

                if (logOk) {
                    logger.logOk(test, Duration.between(timeBefore, timeAfter));
                } else {
                    logger.logFail(test, Duration.between(timeBefore, timeAfter));
                }
            } finally {
                // I really want to run this
                afterClass.finishTest();
            }
        }
    }
}
