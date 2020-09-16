package com.order.manager.utils;

import com.order.manager.model.Order;
import com.order.manager.model.OrderLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculateOrder {
    public void calculateOrderTotalPrice(Order order){
        order.getOrderLines().forEach(orderLine -> orderLine.setLineSubTotal(
                BigDecimal.valueOf(orderLine.getQuantity()).multiply(orderLine.getUnitPrice())));
        order.setTotalPrice(order.getOrderLines().stream().map(OrderLine::getLineSubTotal).reduce(BigDecimal::add).orElse(null));//可以跑一个exception
    }
}
