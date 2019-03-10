package com.example.reflector.test;

import java.awt.print.Printable;

public class ClassWithGenerics2<U, V extends U> {
    U fieldA;

    // same
    <T> T genericMethodA(T arg0) {
        return arg0;
    }

    // changes letter for type
    <W> W genericMethodB(W arg0) {
        return arg0;
    }


    void genericMethodC(java.util.Set<?> arg0) {} // ? extends Object vs ? test

    void genericMethodD(java.util.Set<?> arg0) {} // ? extends String vs ? test
}
