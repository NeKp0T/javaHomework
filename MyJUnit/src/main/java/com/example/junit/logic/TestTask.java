package com.example.junit.logic;

import com.example.junit.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class TestTask implements Runnable {
    private Class<?> testClass;
    private TestLogger logger;

    public TestTask(Class<?> classToTest, TestLogger logger) {
        this.testClass = classToTest;
        this.logger = logger;
    }

    @Override
    public void run() {
        if (needsLaunch(testClass)) {
            Constructor testClassConstructor;
            try {
                testClassConstructor = testClass.getConstructor();
            } catch (NoSuchMethodException e) {
                logger.logNoSuitableConstructor(testClass);
                return;
            }

            Object testClassInstance;
            try {
                testClassInstance = testClassConstructor.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.logNoSuitableConstructor(testClass);
                return;
            } catch (InvocationTargetException e) {
                logger.logConstructorThrows(testClass, e.getTargetException());
                return;
            }

            runClass(testClassInstance);
        }
    }

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

    private void runClass(Object testClassInstance) {
        var beforeClass = getAnnotatedMethods(testClass, BeforeClass.class);
        var afterClass = getAnnotatedMethods(testClass, AfterClass.class);
        var before = getAnnotatedMethods(testClass, Before.class);
        var after = getAnnotatedMethods(testClass, After.class);
        var test = getAnnotatedMethods(testClass, Test.class);

        for (Method beforeClassMethod : beforeClass) {
            tryToInvoke(testClassInstance, beforeClassMethod, BeforeClass.class);
        }

        for (Method testMethod : test) {
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            if (!testAnnotation.ignoreCause().equals(Test.DONT_IGNORE)) {
                logger.logIgnored(testMethod, testAnnotation.ignoreCause());
                continue;
            }

            for (Method beforeMethod : before) {
                tryToInvoke(testClassInstance, beforeMethod, Before.class);
            }

            Class<? extends Throwable> expectedException = testAnnotation.expected();

            boolean logOk = true;
            try {
                testMethod.invoke(testClassInstance);
            } catch (IllegalAccessException e) {
                logger.logIllegalAccess(testMethod, Test.class);
                logOk = false;
            } catch (IllegalArgumentException e) {
                logger.logMethodRequiresArguments(testMethod, Test.class);
                logOk = false;
            } catch (InvocationTargetException e) {
                logOk = false;
                Throwable thrown = e.getTargetException();
                if (expectedException.isInstance(thrown)) {
                    logger.logExpectedThrow(testMethod, thrown);
                } else {
                    logger.logUnexpectedThrow(testMethod, thrown);
                }
            }

            if (logOk) {
                logger.logOk(testMethod);
            }

            for (Method afterMethod : after) {
                tryToInvoke(testClassInstance, afterMethod, After.class);
            }
        }

        for (Method afterClassMethod : afterClass) {
            tryToInvoke(testClassInstance, afterClassMethod, AfterClass.class);
        }
    }

    private static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
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
}
