package com.example.junit.testclasses;

import com.example.junit.annotations.After;
import com.example.junit.annotations.Before;
import com.example.junit.annotations.Test;

public class BeforeAfterTest {

    private int x = 10;

    @Before
    public void before() {
        x = 0;
    }

    @Test
    public void test1() {
        x++;
    }

    @Test
    public void test2() {
        x++;
    }

    @After
    public void after() {
        if (x != 1) {
            throw new RuntimeException();
        }
    }
}
