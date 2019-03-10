package com.example.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassDifferencePrinter extends AbstractReflectorPrinter {
    Class<?> otherClass;

    public static void writeClassesDifference(Class<?> oneClass, Class<?> otherClass, Writer writer) throws IOException {
        new ClassDifferencePrinter(oneClass, otherClass, writer, 0).process();
    }

//    @Override TODO superclass
//    protected void process() throws IOException {
//        processClassNameLine();
//        tabCount++;
//
//        processFields();
//        processMethods();
//        processConstructors();
//        processSubclasses();
//
//        tabCount--;
//        writeLn("}");
//    }

    protected ClassDifferencePrinter(Class<?> oneClass, Class<?> otherClass, Writer writer, int tabCount) {
        this.writer = writer;
        processedClass = oneClass;
        this.otherClass = otherClass;
        this.tabCount = tabCount;
    }

    @Override
    protected void processClassNameLine() throws IOException {
        writer.write("<");
        writeClassName(processedClass);
        writer.write(" {\n");
        writer.write(">");
        writeClassName(otherClass);
        writer.write(" {\n");
    }

    @Override
    protected void processFields() throws IOException {
        Map<String, Field> processedClassFieldsSet = Stream.of(processedClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        Map<String, Field> otherClassFieldsSet = Stream.of(otherClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));


        Field[] processedClassFields = processedClass.getDeclaredFields();
        sortMembers(processedClassFields);
        for (Field processedClassField : processedClassFields) {
            if (otherClassFieldsSet.containsKey(processedClassField.getName())) {
                Field otherClassField = otherClassFieldsSet.get(processedClassField.getName());
                if (!fieldsEquals(processedClassField, otherClassField)) {
                    writer.write("<|");
                    writeField(processedClassField);
                    writer.write(">|");
                    writeField(otherClassField);
                    writer.write("\n");
                }
            } else {
                writer.write("<");
                writeField(processedClassField);
                writer.write("\n");
            }
        }

        Field[] otherClassFields = otherClass.getDeclaredFields();
        sortMembers(otherClassFields);
        for (Field otherClassField : otherClassFields) {
            if (!processedClassFieldsSet.containsKey(otherClassField.getName())) {
                writer.write(">");
                writeField(otherClassField);
                writer.write("\n");
            }
        }
    }

    @Override
    protected void processConstructors() throws IOException {
        // TODO
    }

    @Override
    protected void processSubclasses() throws IOException {
        // do nothing
    }

    @Override
    protected void processMethods() throws IOException {
        Map<String, List<Method>> processedClassMethodsSet = Stream.of(processedClass.getDeclaredMethods())
                .collect(Collectors.groupingBy(Method::getName));

        Map<String, List<Method>> otherClassMethodsSet = Stream.of(otherClass.getDeclaredMethods())
                .collect(Collectors.groupingBy(Method::getName));


        String[] processedClassMethodNames = processedClassMethodsSet.keySet().toArray(String[]::new);
        Arrays.sort(processedClassMethodNames);

        for (String processedClassMethodName : processedClassMethodNames) {
            if (otherClassMethodsSet.containsKey(processedClassMethodName)) {
                List<Method> correspondingOtherClassMethods = otherClassMethodsSet.get(processedClassMethodName);
                List<Method> correspondingProcessedClassMethods = processedClassMethodsSet.get(processedClassMethodName);

                List<Method> uniqueOtherClassMethods = new ArrayList<>();
                List<Method> uniqueProcessedClassMethods = new ArrayList<>();
                uniqueOtherClassMethods.addAll(correspondingOtherClassMethods);
                uniqueProcessedClassMethods.addAll(correspondingProcessedClassMethods);

                removeSimilar(uniqueProcessedClassMethods, correspondingOtherClassMethods, ClassDifferencePrinter::methodsEquals);
                removeSimilar(uniqueOtherClassMethods, correspondingProcessedClassMethods, ClassDifferencePrinter::methodsEquals);

                for (Method processedClassMethod : uniqueProcessedClassMethods) {
                    writer.write("<|");
                    writeMethod(processedClassMethod);
                }
                for (Method otherClassMethod : uniqueOtherClassMethods) {
                    writer.write(">|");
                    writeMethod(otherClassMethod);
                }
            } else {
                for (Method processedClassMethod : processedClassMethodsSet.get(processedClassMethodName)) {
                    writer.write("<");
                    writeMethod(processedClassMethod);
                }
            }
            writer.write("\n");
        }

        Map.Entry<String, List<Method>>[] otherClassMethodEntries = (Map.Entry<String, List<Method>>[]) otherClassMethodsSet.entrySet().toArray(Map.Entry[]::new);
        Arrays.sort(otherClassMethodEntries, Comparator.comparing(Map.Entry::getKey));
        for (Map.Entry<String, List<Method>> otherClassMethodsByNameEntry : otherClassMethodEntries) {
            if (!processedClassMethodsSet.containsKey(otherClassMethodsByNameEntry.getKey())) {
                for (Method otherClassMethod : otherClassMethodsByNameEntry.getValue()) {
                    writer.write(">");
                    writeMethod(otherClassMethod);
                }
                writer.write("\n");
            }
        }
    }

    private static <T, U> void removeSimilar(List<T> listToRemove, List<U> listToLookUp, BiPredicate<T, U> predicate) {
        listToRemove
                .removeIf(
                        objectToRemove -> listToLookUp.stream()
                                .anyMatch(lookedUpObject -> predicate.test(objectToRemove, lookedUpObject))
                );
    }

    private void writeMethod(Method method) throws IOException {
        writeModifiers(method.getModifiers());
        writeType(method.getGenericReturnType());
        writer.write(" ");
        writer.write(method.getName());
        writeArguments(method);
        writeExceptions(method);
        writer.write("\n");
    }

    // doesn't take name in comparision
    private boolean fieldsEquals(Field processedClassField, Field otherClassField) {
        return processedClassField.getModifiers() == otherClassField.getModifiers()
                && processedClassField.getGenericType().equals(otherClassField.getGenericType()); // TODO is it ok?
    }

    private static boolean methodsEquals(Method oneMethod, Method otherMethod) {
        return oneMethod.getGenericReturnType().equals(otherMethod.getGenericReturnType()) // TODO ?? and in executableExuals too too
                && executableEquals(oneMethod, otherMethod);
    }

    private static boolean executableEquals(Executable oneExecutable, Executable otherExecutable) {
        return oneExecutable.getModifiers() == otherExecutable.getModifiers()
                && Arrays.equals(oneExecutable.getGenericParameterTypes(), otherExecutable.getGenericParameterTypes())
                && Arrays.equals(oneExecutable.getGenericExceptionTypes(), otherExecutable.getGenericExceptionTypes());
    }
}
