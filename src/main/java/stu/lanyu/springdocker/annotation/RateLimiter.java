package stu.lanyu.springdocker.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int limit() default 5;
    int timeout() default 1000;
}