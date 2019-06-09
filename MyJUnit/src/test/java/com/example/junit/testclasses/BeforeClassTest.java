package com.example.junit.testclasses;

import com.example.junit.annotations.BeforeClass;
import com.example.junit.annotations.Test;

public class BeforeClassTest {
    private static int x;

    @BeforeClass
    public static void beforeClass() {
        x = 1;
    }

    @Test
    public void test() {
        if (x != 1) {
            throw new RuntimeException();
        }
    }
}
