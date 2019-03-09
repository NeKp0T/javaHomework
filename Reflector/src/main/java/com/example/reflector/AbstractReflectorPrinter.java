package com.example.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;

abstract class AbstractReflectorPrinter {
    Class<?> processedClass;
    Writer writer;
    int tabCount;

    // TODO delete
//    protected abstract void processConstructor(Constructor constructor) throws IOException;
//
//    protected abstract void processMethod(Method method) throws IOException;
//
    protected abstract void processClassNameLine() throws IOException;
//
//    protected abstract void processField(Field field) throws IOException;
//
//    protected abstract void processSubclass(Class<?> i) throws IOException;

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
    
    protected void sortMembers(Member[] fields) {
        Arrays.sort(fields, Comparator.comparing(Member::getName));
    }

}
