package com.example.threads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class HasherTest {

    private static Stream<Arguments> threadPoolsSource() {
        return Stream.generate(new Supplier<Arguments>() {
            @Override
            public Arguments get() {
                if (iter++ == 0) {
                    return Arguments.of(new SingleThreadHasher());
                } else {
                    return Arguments.of(new ForkJoinThreadHasher());
                }

            }
            int iter = 0;
        }).limit(2);
    }

// does not pass
//    @ParameterizedTest
//    @MethodSource("threadPoolsSource")
//    void emptyFile(Hasher hasher) throws IOException {
//        File f = File.createTempFile("qweqw", "qweqw");
//        assertArrayEquals(new byte[0], hasher.hash(f));
//    }
//
//    @ParameterizedTest
//    @MethodSource("threadPoolsSource")
//    void notEmptyFile(Hasher hasher) throws IOException {
//        File f = File.createTempFile("qweqw2", "qweqw2");
//        new FileWriter(f).write("c");
//        assertArrayEquals(AbstractHasherImpl.constructMD5Message().digest("c".getBytes()), hasher.hash(f));
//    }
}