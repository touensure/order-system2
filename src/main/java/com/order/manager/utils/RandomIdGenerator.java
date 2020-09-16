package com.order.manager.utils;

import org.springframework.stereotype.Component;

import static com.order.manager.constant.Constant.ORDER_ID_LENGTH;

@Component
public class RandomIdGenerator {
    public String orderIdGenerator(){
        return String.valueOf( (long)((Math.random()*9+1)*Math.pow(10,ORDER_ID_LENGTH-1)) );
    }
}
