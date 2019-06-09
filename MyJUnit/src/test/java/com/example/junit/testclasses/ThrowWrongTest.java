package com.example.junit.testclasses;

import com.example.junit.annotations.Test;

public class ThrowWrongTest {
    @SuppressWarnings("ConstantConditions")
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test() {
        String s = null;
        //noinspection ResultOfMethodCallIgnored
        s.length();
    }
}
