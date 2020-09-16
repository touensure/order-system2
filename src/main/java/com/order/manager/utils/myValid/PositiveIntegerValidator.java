package com.order.manager.utils.myValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveIntegerValidator implements ConstraintValidator<PositiveInteger, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        return value.toString().matches("^[1-9]*");
    }

}
