package com.example.reflector.test;

public class ClassWithGenericMethods {
    <T> T aMethod(T arg0) {
        throw new UnsupportedOperationException();
    }
    <W> W bMethod(W[] arg0) {
        throw new UnsupportedOperationException();
    }
    <U> U cMethod(java.util.Set<? extends U> arg0, java.util.Set<? super U> arg1) {
        throw new UnsupportedOperationException();
    }
    <X, Y> void dMethod(X[] arg0, Y arg1) {
        throw new UnsupportedOperationException();
    }
    <Z> ClassWithGenericMethods(Z arg0) {
        throw new UnsupportedOperationException();
    }
}
