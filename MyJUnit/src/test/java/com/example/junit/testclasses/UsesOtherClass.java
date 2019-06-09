package com.example.junit.testclasses;

import com.example.junit.annotations.Test;

public class UsesOtherClass {
    @Test
    public void useSimpleTest() {
        new SimpleTest().testMethod();
    }
}
