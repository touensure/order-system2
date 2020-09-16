package com.order.manager.dto.order;

import com.order.manager.dto.orderLine.OrderLineDto;
import com.order.manager.enums.OrderLineStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDto {

    private String orderId;

    private Date createAt;

    private List<OrderLineDto> orderLines;

    private BigDecimal totalPrice;

    private OrderLineStatus status;

    private String customerEmail;
}
