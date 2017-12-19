package stu.lanyu.springdocker.annotation;

import stu.lanyu.springdocker.config.GlobalConfig;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Approve {
    String role() default GlobalConfig.WebConfig.DEFAULT_ROLE;
}
