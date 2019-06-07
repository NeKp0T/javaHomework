package com.example.junit.logic;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Test runner can run test methods in all classes
 * in .class or .jar files in provided directory.
 *
 * All classes are loaded prior to execution.
 *
 * Any methods are executed only in classes that contain at least
 * one method annotated with {@code @Test}.
 *
 * Each class's methods are executed in the same thread, but
 * generally <code>Runtime.getRuntime().availableProcessors()</code>
 * threads used.
 *
 * It continues to execute methods even if any of previous
 * executed methods failed.
 */
public class TestRunner {
    private final ExecutorService testExecutor;
    private final List<TestLogger> loggers = new LinkedList<>();
    private final List<Class<?>> classes = new LinkedList<>();

    /**
     * Constructs a new <code>TestRunner</code>
     */
    public TestRunner() {
        testExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * @param file   a single file or a directory to search for them.
     * @param output where to write test logs
     * @throws FileNotFoundException if provided file does not exists

     */
    public void test(File file, PrintStream output) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        URLClassLoader classLoader;
        try {
            classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // should not happen
        }

        int pathLength = file.getAbsoluteFile().toPath().getNameCount();
        if (file.isFile()) {
            loadFile(classLoader, file, pathLength);
        }

        if (file.isDirectory()) {
            loadDirectory(classLoader, file, pathLength);
        }

        var latch = new CountDownLatch(classes.size());
        for (Class<?> clazz : classes) {
            testClass(clazz, latch);
        }


        try {
            latch.await();
        } catch (InterruptedException e) {
            testExecutor.shutdownNow();
            return;
        }

        for (TestLogger logger : loggers) {
            logger.writeToOutput(output);
        }
    }

    private void testClass(Class<?> classToTest, CountDownLatch latch) {
        var logger = new TestLogger();
        loggers.add(logger);
        testExecutor.submit(new TestClassTask(classToTest, logger, testExecutor, latch));
    }

    private void loadDirectory(URLClassLoader classLoader, File directory, int stripFrom) {
        for (File f : directory.listFiles()) {
            if (f.isFile()) {
                loadFile(classLoader, f, stripFrom);
            }
            if (f.isDirectory()) {
                loadDirectory(classLoader, f, stripFrom);
            }
        }
    }

    private void loadJar(String pathToJar) throws IOException {
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }

            String className = je.getName().substring(0,je.getName().length() - ".class".length());
            className = className.replace('/', '.');
            try {
                Class c = cl.loadClass(className);
                classes.add(c);
            } catch (ClassNotFoundException ex) {
                // should not happen
            }

        }
    }

    private void loadFile(URLClassLoader classLoader, File file, int stripFrom) {
        Path filePath = file.getAbsoluteFile().toPath();
        String fileWithPackage = filePath.subpath(stripFrom, filePath.getNameCount()).toString();

        if (fileWithPackage.endsWith(".class")) {
            String className = fileWithPackage.substring(0, fileWithPackage.lastIndexOf('.')).replace(File.separatorChar, '.');
            try {
                classes.add(classLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                // should not happen
            }
        }
        if (fileWithPackage.endsWith(".jar")) {
            try {
                loadJar(fileWithPackage);
            } catch (IOException e) {
                // this too should not happen
            }
        }
    }
}
