package com.order.manager.dto.order;

import com.order.manager.dto.orderLine.AddOrderLineRequest;
import com.order.manager.enums.OrderLineStatus;
import com.order.manager.model.Account;
import com.order.manager.model.Order;
import com.order.manager.model.OrderLine;
import com.order.manager.utils.RandomIdGenerator;
import lombok.Data;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Component
public class PostOrderRequest {

    @Valid
    private List<AddOrderLineRequest> orderLinesRequest;

    public Order toOrder(Account account){

        MapperFacade mapper = getMapper();
        Order order =  Order.builder()
                            .createAt(new Date())
                            .status(OrderLineStatus.DRAFT)
                            .accountName(account.getAccountName())
                            .customerEmail(account.getEmail())
                            .build();
        List<OrderLine> newOrderLines = new ArrayList<>();
        this.orderLinesRequest.forEach(ol -> { OrderLine orderLine = mapper.map(ol,OrderLine.class);
                                               newOrderLines.add(orderLine);});

        newOrderLines.forEach(orderLine -> {
            orderLine.setOrder(order);
            orderLine.setUuid(UUID.randomUUID().toString());
        });
        order.setOrderLines(newOrderLines);
        return order;
    }

    public Order updateOrder(Order existingOrder){
        MapperFacade mapper = getMapper();
        List<Integer> existingLineNumbers = existingOrder.getOrderLines().stream().map(OrderLine::getLineNumber).collect(Collectors.toList());
        List<OrderLine> newOrderLines = this.orderLinesRequest.stream().map(ol -> {
            OrderLine orderLine = mapper.map(ol,OrderLine.class);
            existingLineNumbers.removeIf(ln -> ln.equals(orderLine.getLineNumber()));
            return orderLine;
        }).collect(Collectors.toList());
        newOrderLines.addAll(existingLineNumbers.stream().map(existingOrder::lineOfnumber).filter(Objects::nonNull).collect(Collectors.toList()));
        newOrderLines.forEach(orderLine -> {
            orderLine.setOrder(existingOrder);
            orderLine.setUuid(UUID.randomUUID().toString());
        });
        existingOrder.setOrderLines(newOrderLines);
        return existingOrder;
    }

    private MapperFacade getMapper(){
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(AddOrderLineRequest.class, OrderLine.class);
        return mapperFactory.getMapperFacade();
    }
}

