package com.example.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;

class ClassStructurePrinter extends AbstractReflectorPrinter {

    public static void writeClass(Class<?> someClass, Writer writer) throws IOException {
        new ClassStructurePrinter(someClass, writer, 0).process();
    }

    protected ClassStructurePrinter(Class<?> someClass, Writer writer, int tabCount) {
        this.writer = writer;
        this.processedClass = someClass;
        this.tabCount = tabCount;
    }

    @Override
    protected void processFields() throws IOException {
        Field[] fields = processedClass.getDeclaredFields();
        sortMembers(fields);
        for (Field field : fields) {
            if (field.isSynthetic()) {
                continue;
            }
            processField(field);
        }
        writeLn("");
    }

    @Override
    protected void processMethods() throws IOException {
        Method[] methods = processedClass.getDeclaredMethods();
        sortMembers(methods);
        for (Method method : methods) {
            processMethod(method);
        }
        writeLn("");
    }

    @Override
    protected void processConstructors() throws IOException {
        Constructor<?>[] constructors = processedClass.getDeclaredConstructors();
        sortMembers(constructors);
        for (Constructor<?> i : constructors) {
            processConstructor(i);
        }
        writeLn("");
    }

    @Override
    protected void processSubclasses() throws IOException {
        Class<?>[] subclasses = processedClass.getDeclaredClasses();
        for (Class<?> i : subclasses) {
            processSubclass(i);
        }
        writeLn("");
    }

    @Override
    protected void processClassNameLine() throws IOException {
        writeClassName(processedClass);
        writer.write(" {\n");
    }

    public void processSubclass(Class<?> someClass) throws IOException {
        new ClassStructurePrinter(someClass, writer, tabCount).process();

    }

    protected void processField(Field field) throws IOException {
        writeField(field);
    }

    protected void processConstructor(Constructor constructor) throws IOException {
        writeModifiers(constructor.getModifiers());
        writeTypeParameters(constructor);
        writer.write(processedClass.getSimpleName());
        writeArgumentsExceptionsAndBody(constructor);
    }

    protected void processMethod(Method method) throws IOException {
        writeModifiers(method.getModifiers());
        writeTypeParameters(method);
        writeType(method.getGenericReturnType());
        writer.append(" ");
        writer.write(method.getName());
        writeArgumentsExceptionsAndBody(method);
    }
}
