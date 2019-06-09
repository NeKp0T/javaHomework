package com.example.junit.testclasses;

import com.example.junit.annotations.AfterClass;
import com.example.junit.annotations.Test;

public class AfterClassTest {
    private static int x;

    @Test
    public void test() {
        x = 1;
    }

    @AfterClass
    public static void afterClass() {
        if (x != 1) {
            throw new RuntimeException();
        }
    }
}
