package com.order.manager.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderLineNotFoundException extends RuntimeException{
    private static final String MESSAGE = "orderLine:%s doesn't exist";

    public OrderLineNotFoundException(final String orderLineUuid) { super(String.format(MESSAGE, orderLineUuid));}
}
