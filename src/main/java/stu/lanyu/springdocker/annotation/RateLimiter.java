package stu.lanyu.springdocker.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int qps() default 1000;
    int interval() default 60000;
    int minFrequencyInInterval() default 10;
}