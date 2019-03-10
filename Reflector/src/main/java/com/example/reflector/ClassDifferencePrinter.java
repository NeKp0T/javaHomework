package com.example.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassDifferencePrinter extends AbstractReflectorPrinter {
    private final Class<?> otherClass;

    public static void writeClassesDifference(Class<?> oneClass, Class<?> otherClass, Writer writer) throws IOException {
        new ClassDifferencePrinter(oneClass, otherClass, writer, 0).process();
    }

    protected ClassDifferencePrinter(Class<?> oneClass, Class<?> otherClass, Writer writer, @SuppressWarnings("SameParameterValue") int tabCount) {
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
        Map<String, Field> processedClassFieldsSet = getAllFieldsStream(processedClass)
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        Map<String, Field> otherClassFieldsSet = getAllFieldsStream(otherClass)
                .collect(Collectors.toMap(Field::getName, Function.identity()));


        Field[] processedClassFields = processedClassFieldsSet.values().toArray(Field[]::new);
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

        Field[] otherClassFields = otherClassFieldsSet.values().toArray(Field[]::new);
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
        List<Constructor> correspondingOtherClassMethods = List.of(otherClass.getConstructors());
        List<Constructor> correspondingProcessedClassMethods = List.of(processedClass.getConstructors());

        List<Constructor> uniqueOtherClassMethods = new ArrayList<>(correspondingOtherClassMethods);
        List<Constructor> uniqueProcessedClassMethods = new ArrayList<>(correspondingProcessedClassMethods);

        removeSimilar(uniqueProcessedClassMethods, correspondingOtherClassMethods, ClassDifferencePrinter::executableEquals);
        removeSimilar(uniqueOtherClassMethods, correspondingProcessedClassMethods, ClassDifferencePrinter::executableEquals);

        for (Constructor processedClassMethod : uniqueProcessedClassMethods) {
            writer.write("<|");
            writeConstructor(processedClassMethod);
        }
        for (Constructor otherClassMethod : uniqueOtherClassMethods) {
            writer.write(">|");
            writeConstructor(otherClassMethod);
        }
    }

    @Override
    protected void processSubclasses() throws IOException {
        // do nothing
    }

    @Override
    protected void processMethods() throws IOException {
        try {
            printChangingExecutables(getAllMethodsStream(processedClass),
                    getAllMethodsStream(otherClass),
                    ClassDifferencePrinter::methodsEquals,
                    method -> {
                        try {
                            writeMethod(method);
                        } catch (IOException e) {
                            throw new IOExceptionButRuntime(e);
                        }
                    }
            );
        } catch (IOExceptionButRuntime e) {
            throw e.actualException;
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

    private void writeConstructor(Constructor constructor) throws IOException {
        writeModifiers(constructor.getModifiers());
        writer.write(" ");
        writer.write(constructor.getName());
        writeArguments(constructor);
        writeExceptions(constructor);
        writer.write("\n");
    }

    // doesn't take name in comparision
    private boolean fieldsEquals(Field processedClassField, Field otherClassField) {
        return processedClassField.getModifiers() == otherClassField.getModifiers()
                && processedClassField.getGenericType().equals(otherClassField.getGenericType()); // looks like it's working
    }

    private static boolean methodsEquals(Method oneMethod, Method otherMethod) {
        return oneMethod.getGenericReturnType().equals(otherMethod.getGenericReturnType()) // looks like it's working
                && executableEquals(oneMethod, otherMethod);
    }

    private static boolean executableEquals(Executable oneExecutable, Executable otherExecutable) {
        return oneExecutable.getModifiers() == otherExecutable.getModifiers()
                && Arrays.equals(oneExecutable.getGenericParameterTypes(), otherExecutable.getGenericParameterTypes())
                && Arrays.equals(oneExecutable.getGenericExceptionTypes(), otherExecutable.getGenericExceptionTypes());
    }

    private static Stream<Method> getAllMethodsStream(Class<?> someClass) {
        Stream<Method> allMethodsStream = Stream.empty();
        for (Class<?> superClass = someClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
            allMethodsStream = Stream.concat(allMethodsStream, Stream.of(superClass.getDeclaredMethods()));
        }
        return allMethodsStream;
    }

    private Stream<Field> getAllFieldsStream(Class<?> someClass) {
        Stream<Field> allFieldsStream = Stream.empty();
        for (Class<?> superClass = someClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
            allFieldsStream = Stream.concat(allFieldsStream, Stream.of(superClass.getDeclaredFields()));
        }
        return allFieldsStream;
    }

    private <T extends Executable> void printChangingExecutables(Stream<T> processedClassStream, Stream<T> otherClassStream, BiPredicate<T, T> compare, Consumer<T> print) throws IOException {
        Map<String, List<T>> processedClassMethodsSet = processedClassStream
                .collect(Collectors.groupingBy(Executable::getName));

        Map<String, List<T>> otherClassMethodsSet = otherClassStream
                .collect(Collectors.groupingBy(Executable::getName));


        String[] processedClassMethodNames = processedClassMethodsSet.keySet().toArray(String[]::new);
        Arrays.sort(processedClassMethodNames);

        for (String processedClassMethodName : processedClassMethodNames) {
            if (otherClassMethodsSet.containsKey(processedClassMethodName)) {
                List<T> correspondingOtherClassMethods = otherClassMethodsSet.get(processedClassMethodName);
                List<T> correspondingProcessedClassMethods = processedClassMethodsSet.get(processedClassMethodName);

                List<T> uniqueOtherClassMethods = new ArrayList<>(correspondingOtherClassMethods);
                List<T> uniqueProcessedClassMethods = new ArrayList<>(correspondingProcessedClassMethods);

                removeSimilar(uniqueProcessedClassMethods, correspondingOtherClassMethods, compare);
                removeSimilar(uniqueOtherClassMethods, correspondingProcessedClassMethods, compare);

                for (T processedClassMethod : uniqueProcessedClassMethods) {
                    writer.write("<|");
                    print.accept(processedClassMethod);
                }
                for (T otherClassMethod : uniqueOtherClassMethods) {
                    writer.write(">|");
                    print.accept(otherClassMethod);
                }
            } else {
                for (T processedClassMethod : processedClassMethodsSet.get(processedClassMethodName)) {
                    writer.write("<");
                    print.accept(processedClassMethod);
                }
            }
            writer.write("\n");
        }

        Map.Entry<String, List<T>>[] otherClassMethodEntries = (Map.Entry<String, List<T>>[]) otherClassMethodsSet.entrySet().toArray(Map.Entry[]::new);
        Arrays.sort(otherClassMethodEntries, Comparator.comparing(Map.Entry::getKey));
        for (Map.Entry<String, List<T>> otherClassMethodsByNameEntry : otherClassMethodEntries) {
            if (!processedClassMethodsSet.containsKey(otherClassMethodsByNameEntry.getKey())) {
                for (T otherClassMethod : otherClassMethodsByNameEntry.getValue()) {
                    writer.write(">");
                    print.accept(otherClassMethod);
                }
                writer.write("\n");
            }
        }
    }

    static private class IOExceptionButRuntime extends RuntimeException {
        final public IOException actualException;
        public IOExceptionButRuntime(IOException exception) {
            actualException = exception;
        }
    }
}
