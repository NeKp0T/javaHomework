package com.example.reflector.test;

public class ClassSuperclass1 {
    int superFieldA; // same
    int superFieldB; // changes type
    public int superFieldC; // changes visibility
    int superFieldD; // only here

    int superMethodA(int arg0) { return 0; } // same
    int superMethodB(int arg0) { return 0; } // changes return type
    int superMethodC(int arg0) { return 0; } // changes argument type
    public int superMethodD(int arg0) { return 0; } // changes visibility
    static int superMethodE(int arg0) { return 0; } // changes other modifiers
    int superMethodF(int arg0) { return 0; } // present only here
}
