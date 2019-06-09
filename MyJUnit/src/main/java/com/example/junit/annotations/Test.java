package com.example.junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * {@code @Test} is used to signal that the annotated method is a
 * test method.
 *
 * Test methods should be public, non-static and should not have
 * any arguments.
 *
 * Test is considered passed, if it is not ignored and it does not
 * throw any exceptions or throws an exception of expected type.
 *
 * Test is ignored if it has any ignoreCause except "N/A".
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * Expected exception type.
     */
    Class<? extends Throwable> expected() default ThrowNothing.class;

    /**
     * Cause to ignore test. If set to anything except empty string, test will be ignored.
     */
    String ignoreCause() default DONT_IGNORE;

    String DONT_IGNORE = "";
}
