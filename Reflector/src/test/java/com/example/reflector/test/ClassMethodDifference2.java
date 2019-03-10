package com.example.reflector.test;

public class ClassMethodDifference2 {
    void presentOnlyInSecond() {}

    Integer changesReturnType() { return null; }

    private void changesVisibility() {}

    void changesArgumentType(long arg0) {}

    void changesArgumentCount(int arg0, int arg1, int arg2) {}

    public String sameFunction(int arg0, long arg1, String arg2) { return null; }
}
