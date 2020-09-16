package com.order.manager.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderNotFoundException  extends RuntimeException{
    private static final String MESSAGE = "order:%s doesn't exist when updating an order";

    public OrderNotFoundException(final String orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
