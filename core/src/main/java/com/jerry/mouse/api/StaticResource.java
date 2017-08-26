package com.jerry.mouse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置静态资源映射
 */
@ConfigAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticResource {

    String prefix();

    String target();

    int expires() default 600;

    String[] cacheExtensions() default {"html","htm","js","css","jpg","jpeg","gif","png","bmp","mp3","avi","mpeg","mov",
            "mp4","ogg","ape","doc","docx","xls","xlsx","ppt","pptx"};
}
