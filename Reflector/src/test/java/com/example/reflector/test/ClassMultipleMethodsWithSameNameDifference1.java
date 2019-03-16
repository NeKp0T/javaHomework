package com.example.reflector.test;

public class ClassMultipleMethodsWithSameNameDifference1 {
    void method() {} // only in first
    int method(int arg0) { return 0; } // changes return type
    public void method(int arg0, int arg1) {} // changes visibility
    public int method(String stringArg) { return 0; } // doesn't change
}

