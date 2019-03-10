package com.example.reflector.test;

import java.util.Map;

public class ClassWithExtends<T extends String> {
    <U extends T> void method(U arg0, T arg1) {
        arg1 = arg0;
    };
}
