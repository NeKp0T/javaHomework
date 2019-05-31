package com.example.junit.testclasses;

import com.example.junit.annotations.Test;

public class IgnoreTest {
    @Test(ignoreCause = "fortest")
    void test() {
    }
}
