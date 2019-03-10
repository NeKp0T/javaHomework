package com.example.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Abstract superclass for classes, that write information about specific class into specific writer.
 * Provides some methods for writing information about classes, and default realization of some logic.
 */
abstract class AbstractReflectorPrinter {
    Class<?> processedClass;
    Writer writer;
    int tabCount;

    protected void process() throws IOException {
        processClassNameLine();
        tabCount++;

        processFields();
        processMethods();
        processConstructors();
        processSubclasses();

        tabCount--;
        writeLn("}");
    }

    protected abstract void processClassNameLine() throws IOException;

    abstract protected void processFields() throws IOException;

    abstract protected void processConstructors() throws IOException;

    abstract protected void processSubclasses() throws IOException;

    protected void writeTabs() throws IOException {
        for (int i = 0; i < tabCount; i++) {
            writer.append('\t');
        }
    }

    protected void writeLn(String string) throws IOException {
        writeTabs();
        writer.write(string + "\n");
    }

    protected void writeModifiers(int modifiers) throws IOException {
        writeTabs();
        if (Modifier.isPublic(modifiers)) {
            writer.write("public ");
        }
        if (Modifier.isProtected(modifiers)) {
            writer.write("protected ");
        }
        if (Modifier.isPrivate(modifiers)) {
            writer.write("private ");
        }
        if (Modifier.isAbstract(modifiers)) {
            writer.write("abstract ");
        }
        if (Modifier.isStatic(modifiers)) {
            writer.write("static ");
        }
        if (Modifier.isFinal(modifiers)) {
            writer.write("final ");
        }
        if (Modifier.isTransient(modifiers)) {
            writer.write("transient ");
        }
        if (Modifier.isVolatile(modifiers)) {
            writer.write("volatile ");
        }
        if (Modifier.isSynchronized(modifiers)) {
            writer.write("synchronised ");
        }
        if (Modifier.isNative(modifiers)) {
            writer.write("native ");
        }
        if (Modifier.isStrict(modifiers)) {
            writer.write("strictfp ");
        }

    }

    protected void writeType(Type type) throws IOException {
        writer.write(type.getTypeName().replaceAll("\\$", "."));
    }

    protected void writeArguments(Executable executable) throws IOException {
        writer.write("(");
        Type[] argumentTypes = executable.getGenericParameterTypes();
//                TypeVariable<Method>[] typeParameters = executable.getTypeParameters();
        for (int i = 0; i < argumentTypes.length; i++) {
            writeType(argumentTypes[i]);
            writer.write(" arg" + i);
            if (i + 1 != argumentTypes.length) {
                writer.write(", ");
            }
        }
        writer.write(") ");

    }

    protected void writeExceptions(Executable executable) throws IOException {
        Type[] exceptionTypes = executable.getGenericExceptionTypes();
        if (exceptionTypes.length != 0) {
            writer.write("throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                Type exceptionType = exceptionTypes[i];
                writeType(exceptionType);
                if (i + 1 < exceptionTypes.length) {
                    writer.write(", ");
                }
            }
        }
    }

    protected void writeDefaultFunctionBody() throws IOException {
        writer.write("{\n");
        tabCount++;
        writeLn("throw new UnsupportedOperationException();");
        tabCount--;
        writeLn("}\n");
    }

    protected void writeArgumentsExceptionsAndBody(Executable executable) throws IOException {
        writeArguments(executable);
        writeExceptions(executable);
        writeDefaultFunctionBody();
    }

    abstract protected void processMethods() throws IOException;

    protected void writeTypeParameters(Executable executable) throws IOException {
        TypeVariable[] typeVariables = executable.getTypeParameters();

        if (typeVariables.length == 0) {
            return;
        }

        writer.write("<");
        for (int i = 0; i < typeVariables.length; i++) {
            writer.write(typeVariables[i].getName());
            if (i + 1 < typeVariables.length) {
                writer.write(", ");
            }
        }
        writer.write("> ");
    }
    
    protected void sortMembers(Member[] members) {
        Arrays.sort(members, Comparator.comparing(Member::getName));
    }

    protected <T> void writeClassName(Class<T> someClass) throws IOException {
        //writeTabs();
        writeModifiers(someClass.getModifiers());
        writer.write("class ");
        writer.write(someClass.getSimpleName());

        TypeVariable<Class<T>>[] typeParameters = someClass.getTypeParameters();
        if (typeParameters.length != 0) {
            writer.write("<");

            for (int i = 0; i < typeParameters.length; i++) {
                TypeVariable<Class<T>> type = typeParameters[i];
                writer.write(type.toString());

                List<Type> bounds = List.of(type.getBounds())
                        .stream()
                        .filter(boundType -> !boundType.getTypeName().equals("java.lang.Object"))
                        .collect(Collectors.toList());

                if (bounds.size()> 0) {
                    writer.write(" extends ");
                    Iterator<Type> iterator = bounds.iterator();
                    writeType(iterator.next());
                    while (iterator.hasNext()) {
                        writer.write(" & ");
                        writeType(iterator.next());
                    }
                }
                if (i + 1 < typeParameters.length) {
                    writer.write(", ");
                }
            }


            writer.write(">");
        }

        if (someClass.getSuperclass() != Object.class) {
            writer.write(" extends " + someClass.getSuperclass().getName());
        }
    }

    protected void writeField(Field field) throws IOException {
        writeModifiers(field.getModifiers());
        writeType(field.getGenericType());
        writer.append(" ");
        writer.write(field.getName());
        writer.write(";\n");
    }
}
