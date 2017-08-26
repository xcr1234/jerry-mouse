package com.jerry.mouse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ConfigAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionConfig {

    int maxAge() default 1800;

    String cookieName() default "MSessionId";

}
