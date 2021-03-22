package com.order.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthorizationFailedException extends RuntimeException{
    public AuthorizationFailedException(final String msg) {
        super(msg);
    }
}
