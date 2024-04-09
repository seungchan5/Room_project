package project.study.customAnnotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathRoom {

    @AliasFor("name")
    String value() default  "";

    @AliasFor("value")
    String name() default "";

    boolean required() default true;

}
