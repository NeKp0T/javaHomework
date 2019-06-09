package com.example.junit.testclasses;

import com.example.junit.annotations.Test;

public class ThrowCorrectTest {
    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void test() {
        String s = null;
        //noinspection ResultOfMethodCallIgnored
        s.length();
    }
}
