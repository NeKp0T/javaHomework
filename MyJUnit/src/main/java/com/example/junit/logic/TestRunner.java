package com.example.junit.logic;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class TestRunner {
    private final ExecutorService testExecutor;
    private final List<TestLogger> loggers = new LinkedList<>();
    private final List<Class<?>> classes = new LinkedList<>();

    public TestRunner() {
        testExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void test(File file, PrintStream output) throws IOException {
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

        var futures = new LinkedList<Future>();
        for (Class<?> clazz : classes) {
            futures.add(testClass(clazz));
        }

        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                testExecutor.shutdownNow();
                return;
            } catch (ExecutionException e) {
                throw new RuntimeException(e); // should not happen
            }
        }

        for (TestLogger logger : loggers) {
            logger.writeToOutput(output);
        }
    }

    private Future testClass(Class<?> classToTest) {
        var logger = new TestLogger();
        loggers.add(logger);
        return testExecutor.submit(new TestTask(classToTest, logger));
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

    private void loadFile(URLClassLoader classLoader, File file, int stripFrom) {
        Path filePath = file.getAbsoluteFile().toPath();
        String fileWithPackage = filePath.subpath(stripFrom, filePath.getNameCount()).toString();
//        System.out.println("file: " + fileWithPackage); TODO remove all this coments

        if (fileWithPackage.endsWith(".class") || fileWithPackage.endsWith(".jar")) {
            String className = fileWithPackage.substring(0, fileWithPackage.lastIndexOf('.')).replace(File.separatorChar, '.');
//            System.out.println("class: " + className);
            try {
                classes.add(classLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
//                System.out.println("Cant load ");
            }
        }
    }
}
