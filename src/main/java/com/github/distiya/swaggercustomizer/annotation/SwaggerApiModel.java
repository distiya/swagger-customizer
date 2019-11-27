package com.github.distiya.swaggercustomizer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SwaggerApiModel {
    Class<?>[] classOrder() default {};
    Class<?> mainClass() default  Object.class;
}
