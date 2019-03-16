package com.example.reflector.test;

public class ClassWithConstructors { // TODO create pair
    ClassWithConstructors(int arg0) {} // same
    ClassWithConstructors(int arg0, int arg1) {} // changes visibility
    ClassWithConstructors(int arg0, int arg1, int arg2) {} // present only here
    ClassWithConstructors(int arg0, int arg1, int arg2, int arg3) {} // changes argument type
}
