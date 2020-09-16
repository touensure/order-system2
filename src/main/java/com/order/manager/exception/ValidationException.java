package com.order.manager.exception;

import com.google.common.collect.ImmutableList;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException{
    private static final String DEFAULT_GLOBAL_MSG = "Error validating your data:";
    private final List<FieldError> fieldErrors;

    public ValidationException(final List<FieldError> fieldErrors) {
        super(DEFAULT_GLOBAL_MSG+fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";")));
        this.fieldErrors = ImmutableList.copyOf(fieldErrors);
    }

    public List<FieldError> getFieldErrors() {
        return this.fieldErrors;
    }
}
