package com.example.reflector.test;

public class ClassMultipleMethodsWithSameNameDifference2 {
    String method(int a, int b, int c, int d) { return ""; } // only in second
    long method(int arg0) { return 0; } // changes return type
    private void method(int arg0, int arg1) {} // changes visibility
    public int method(String stringArg) { return 0; } // doesn't change
}
