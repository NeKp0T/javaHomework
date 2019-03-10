package com.example.reflector.test;

import java.awt.print.Printable;

public class ClassWithGenerics1<U, V extends Printable & Iterable<Integer>, X extends  U> {
    U fieldA;

    void f(U a, V b) {
//        a = b;
    }

    // same
    <T> T genericMethodA(T arg0) {
        return arg0;
    }

    // changes letter for type
    <T> T genericMethodB(T arg0) {
        return arg0;
    }


    void genericMethodC(java.util.Set<? extends Object> arg0) {} // ? extends Object vs ? test

    void genericMethodD(java.util.Set<? extends String> arg0) {} // ? extends String vs ? test
}
