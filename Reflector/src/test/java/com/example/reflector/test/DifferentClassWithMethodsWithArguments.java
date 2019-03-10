package com.example.reflector.test;

class DifferentClassWithMethodsWithArguments {
    void voidFuncFromInt(String arg0) { // different argument type
        throw new UnsupportedOperationException();
    }
    String stringFuncFromString(String arg0) {
        throw new UnsupportedOperationException();
    } // same method
    String multipleArguments(String arg0, Integer arg1, char arg2) {
        throw new UnsupportedOperationException();
    } // different return type
    DifferentClassWithMethodsWithArguments() {
        throw new UnsupportedOperationException();
    }
    DifferentClassWithMethodsWithArguments(int arg0, char arg1, String arg2) {
        throw new UnsupportedOperationException();
    }

    void newMethod() {
        throw new UnsupportedOperationException();
    } // new method
}
