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
    protected Class<?> processedClass;
    protected Writer writer;
    protected int tabCount;

    /**
     * Main method, that starts class processing.
     */
    protected void process() throws IOException {
        processPackage();
        processClassNameLine();
        tabCount++;

        processFields();
        processMethods();
        processConstructors();
        processSubclasses();

        tabCount--;
        writeLn("}");
    }

    /**
     * Processes whole line with class declaration.
     */
    protected abstract void processClassNameLine() throws IOException;

    /**
     * Processes class' package
     */
    abstract protected void processPackage() throws IOException;

    /**
     * Processes all class fields
     */
    abstract protected void processFields() throws IOException;

    /**
     * Processes all class constructors
     */
    abstract protected void processConstructors() throws IOException;

    /**
     * Processes all class subclasses
     */
    abstract protected void processSubclasses() throws IOException;

    /**
     * Processes all class methods
     */
    abstract protected void processMethods() throws IOException;

    /**
     * Writes tabs into <code>writer</code> according to remembered tabCount
     */
    protected void writeTabs() throws IOException {
        for (int i = 0; i < tabCount; i++) {
            writer.append('\t');
        }
    }

    /**
     * Writes one line with tabs
     */
    protected void writeLn(String string) throws IOException {
        writeTabs();
        writer.write(string + "\n");
    }

    /**
     * Writes given modifiers in correct order with spaces between and one space after
     */
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

    /**
     * Writes type name
     */
    protected void writeType(Type type) throws IOException {
        writer.write(type.getTypeName()
                .replaceAll("\\$", ".")
                .replaceAll("(?<=\\A|\\s|[^.\\w])java.lang.", "")
        );
    }

    /**
     * Writes executable's arguments with brackets
     */
    protected void writeArguments(Executable executable) throws IOException {
        writer.write("(");
        Type[] argumentTypes = executable.getGenericParameterTypes();
        for (int i = 0; i < argumentTypes.length; i++) {
            writeType(argumentTypes[i]);
            writer.write(" arg" + i);
            if (i + 1 != argumentTypes.length) {
                writer.write(", ");
            }
        }
        writer.write(") ");

    }

    /**
     * Writes executable's exceptions
     */
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

    /**
     * Writes function body, that throws <code>UnsupportedOperationException</code>
     */
    protected void writeDefaultFunctionBody() throws IOException {
        writer.write("{\n");
        tabCount++;
        writeLn("throw new UnsupportedOperationException();");
        tabCount--;
        writeLn("}\n");
    }

    /**
     * Literally calls methods mentioned in it's name.
     */
    protected void writeArgumentsExceptionsAndBody(Executable executable) throws IOException {
        writeArguments(executable);
        writeExceptions(executable);
        writeDefaultFunctionBody();
    }

    protected void writeTypeParameters(Executable executable) throws IOException {
        TypeVariable[] typeVariables = executable.getTypeParameters();

        if (typeVariables.length == 0) {
            return;
        }

        writer.write("<");
        for (int i = 0; i < typeVariables.length; i++) {
            writeTypeParameter(typeVariables[i]);
            if (i + 1 < typeVariables.length) {
                writer.write(", ");
            }
        }
        writer.write("> ");
    }

    void writeTypeParameter(TypeVariable type) throws IOException {
        writeType(type);

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
    }

    /**
     * Sorts methods by their name
     */
    protected void sortMembers(Member[] members) {
        Arrays.sort(members, Comparator.comparing(Member::getName));
    }

    protected void writePackage(Class<?> someClass) throws IOException {
        writer.write("package ");
        writer.write(someClass.getPackageName());
    }

    /**
     * Writes class declaration line without opening '{'
     */
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
                writeTypeParameter(type);
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

    /**
     * Writes field declaration including ';'
     */
    protected void writeField(Field field) throws IOException {
        writeModifiers(field.getModifiers());
        writeType(field.getGenericType());
        writer.append(" ");
        writer.write(field.getName());
        writer.write(";\n");
    }
}
