package com.order.manager.utils.myValid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PositiveIntegerValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveInteger {

    String message() default "{com.order.manager.utils.myValid.PositiveInteger.message:value should be a positive integer}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
