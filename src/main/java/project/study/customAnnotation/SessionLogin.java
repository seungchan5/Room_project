package project.study.customAnnotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SessionLogin {

    boolean required() default false;
    CallType type() default CallType.REST_CONTROLLER;
}
