package com.example.junit.testclasses;

import com.example.junit.annotations.Test;

public class ThrowCorrectTest {
    @Test(expected = NullPointerException.class)
    public void test() {
        String s = null;
        //noinspection ResultOfMethodCallIgnored
        s.length();
    }
}
