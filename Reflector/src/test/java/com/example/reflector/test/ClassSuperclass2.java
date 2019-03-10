package com.example.reflector.test;

public class ClassSuperclass2 {
    int superFieldA; // same
    long superFieldB; // changes type
    private int superFieldC; // changes visibility
    int superFieldNotD; // only here

    int superMethodA(int arg0) { return 0; }; // same
    long superMethodB(int arg0) { return 0; }; // changes return type
    int superMethodC(long arg0) { return 0; }; // changes argument type
    private int superMethodD(int arg0) { return 0; }; // changes visibility
    int superMethodE(int arg0) { return 0; }; // changes other modifiers
    int superMethodNotF(int arg0) { return 0; }; // present only here
}
