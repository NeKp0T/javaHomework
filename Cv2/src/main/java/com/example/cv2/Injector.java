package com.example.cv2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     * // TODO throws and throw not Exception
     */
    @NotNull
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws ClassNotFoundException, IllegalAccessException, AmbiguousImplementationException, ImplementationNotFoundException, InstantiationException, task.InjectionCycleException, InvocationTargetException {
        var implementationClasses = new ArrayList<Class>();
        for (String name : implementationClassNames) {
            implementationClasses.add(Class.forName(name));
        }

        return initializeClassRecursion(Class.forName(rootClassName), implementationClasses, new ArrayList<>(), new TreeMap<>());
    }

    private static boolean mightBeConstructedAs(Class<?> requiredClass, Class<?> possibleClass) {
        if (possibleClass.isInterface() || Modifier.isAbstract(possibleClass.getModifiers())) {
            return false;
        }
        return requiredClass.isAssignableFrom(possibleClass);
    }

    // TODO resolve nullable and notNull
    @NotNull
    private static Class<?> getClassForName(String requiredClassName, List<Class> implementationClasses, List<Class> classesInRecursionStack) throws ClassNotFoundException, AmbiguousImplementationException, ImplementationNotFoundException, task.InjectionCycleException {
        Class<?> requiredClass = Class.forName(requiredClassName); // TODO mb throw smth?

        @Nullable Class<?> implementationClass = null;
        for (Class<?> possibleClass : implementationClasses) {
            if (mightBeConstructedAs(requiredClass, possibleClass)) {
                if (implementationClass != null) {
                    throw new AmbiguousImplementationException();
                }
                implementationClass = possibleClass;
            }
        }

        if (implementationClass == null) {
            throw new ImplementationNotFoundException();
        }

        if (classesInRecursionStack.contains(implementationClass)) {
            throw new task.InjectionCycleException();
        }

        return implementationClass;
    }

    // TODO make line shorter
    @NotNull
    private static Object initializeClassRecursion(Class classToConstruct, List<Class> implementationClasses, List<Class> classesInRecursionStack, Map<String, Object> constructedObjects) throws ClassNotFoundException, AmbiguousImplementationException, task.InjectionCycleException, ImplementationNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructedObjects.containsKey(classToConstruct.getName())) {
            return constructedObjects.get(classToConstruct.getName());
        }

        classesInRecursionStack.add(classToConstruct);

        if (classToConstruct.getConstructors().length != 1) {
            throw new IllegalArgumentException("rootClass has not 1 constructor");
        }

        Constructor<?> constructor = classToConstruct.getConstructors()[0];
        Type[] argumentTypes = constructor.getGenericParameterTypes();
        Object[] arguments = new Object[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            Type type = argumentTypes[i];
            Class<?> argumentClass = getClassForName(type.getTypeName(), implementationClasses, classesInRecursionStack); // TODO catch?
            arguments[i] = initializeClassRecursion(argumentClass, implementationClasses, classesInRecursionStack, constructedObjects);
        }

        Object result = constructor.newInstance(arguments);
        constructedObjects.put(classToConstruct.getName(), result);

        classesInRecursionStack.remove(classesInRecursionStack.size() - 1);

        return result;
    }
}