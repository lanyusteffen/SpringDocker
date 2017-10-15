package stu.lanyu.springdocker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that this should be picked up as a ReadOnly database operation, specifically for
 * Spring Data JPA repositories.
 * @author stelylan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ReadOnly {

}
