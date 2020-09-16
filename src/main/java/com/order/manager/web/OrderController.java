package com.order.manager.web;

import com.order.manager.dto.order.OrderDto;
import com.order.manager.dto.order.PostOrderRequest;
import com.order.manager.exception.ValidationException;
import com.order.manager.service.OrderService;
import com.order.manager.model.Order;
import com.order.manager.model.OrderLine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@EnableSwagger2
@Api(value = "Swagger order manager", description = "basic order manager", tags = "Orders")
@RestController
@RequestMapping("/orders")
@Transactional
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("create new order")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> createOrder(@Validated @RequestBody final PostOrderRequest postOrderRequest,
                                                final BindingResult bindingResult) {
        if (!bindingResult.getAllErrors().isEmpty()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        OrderDto orderDto = orderService.creatOrder(postOrderRequest, null);
        return ResponseEntity.ok(orderDto);
    }

    @ApiOperation("find all order")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderDto>> findAllOrders(@RequestParam(defaultValue = "0") final Integer pageNumber,
                                                        @RequestParam(defaultValue = "10") final Integer pageSize) {
        return ResponseEntity.ok(orderService.findAllOrders(pageNumber, pageSize));
    }

    @ApiOperation(value = "find specified orders", notes = "find order by order Id, multiple order Id is supported")
    @GetMapping(value = "/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderDto>> findOrders(@RequestParam(defaultValue = "0") final Integer pageNumber,
                                                     @RequestParam(defaultValue = "10") final Integer pageSize,
                                                     @RequestParam(defaultValue = "false") final boolean filterCancelledOrder,
                                                     @PathVariable("ids") String ids) {
        List<String> orderIds = List.of(ids.split(","));
        return ResponseEntity
            .ok(orderService.findOrders(pageNumber, pageSize, filterCancelledOrder, orderIds));
    }

    @ApiOperation(value = "update order", notes = "update order by order Id")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> updateOrder(@Validated @RequestBody final PostOrderRequest postOrderRequest,
                                                @PathVariable final String id) {
        OrderDto order = orderService.creatOrder(postOrderRequest, id);
        return ResponseEntity.ok(order);
    }

    @ApiOperation(value = "delete order", notes = "delete order by order Id, multiple order Id is supported")
    @DeleteMapping(value = "/{ids}")
    public ResponseEntity<List<OrderDto>> deleteOrder(@PathVariable final String ids) {
        List<String> orderIds = List.of(ids.split(","));
        List<OrderDto> orders = orderService.deleteOrders(orderIds);
        return ResponseEntity.ok(orders);
    }

    @ApiOperation(value = "cancel orders", notes = "cancel order by order Id, multiple order Id is supported")
    @PostMapping(value = "/{ids}/cancelOrders")
    public ResponseEntity<List<OrderDto>> cancelOrders(@PathVariable final String ids) {
        List<String> orderIds = List.of(ids.split(","));
        List<OrderDto> order = orderService.cancelOrders(orderIds);
        return ResponseEntity.ok(order);
    }

}
