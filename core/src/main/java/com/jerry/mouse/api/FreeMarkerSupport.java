package com.jerry.mouse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ConfigAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FreeMarkerSupport {
    String mapping();

    String base() default "";

    String encoding() default "";
}
