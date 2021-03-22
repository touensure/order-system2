package com.order.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(final String msg) {
        super(msg);
    }
}
