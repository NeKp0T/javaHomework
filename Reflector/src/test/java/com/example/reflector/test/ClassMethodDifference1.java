package com.example.reflector.test;

public class ClassMethodDifference1 {
    void presentOnlyInFirst() {}

    String changesReturnType() { return null; }

    public void changesVisibility() {}

    void changesArgumentType(int arg0) {}

    void changesArgumentCount(int arg0, int arg1) {}

    public String sameFunction(int arg0, long arg1, String arg2) { return null; }
}
