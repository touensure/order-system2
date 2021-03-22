package com.order.manager.web;

import com.order.manager.config.Authorize;
import com.order.manager.dto.order.OrderDto;
import com.order.manager.dto.order.PostOrderRequest;
import com.order.manager.exception.ValidationException;
import com.order.manager.service.OrderService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.order.manager.constant.Constant.ADMINISTRATOR_CUSTOMER_SCOPE;
import static com.order.manager.constant.Constant.COMMA;
import static com.order.manager.constant.Constant.CUSTOMER_SCOPE;

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
    @Authorize(value = CUSTOMER_SCOPE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> createOrder(@Validated @RequestBody final PostOrderRequest postOrderRequest,
                                                final HttpServletRequest httpServletRequest,
                                                final BindingResult bindingResult) {
        if (!bindingResult.getAllErrors().isEmpty()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        OrderDto orderDto = orderService.createOrder(postOrderRequest, null, httpServletRequest);
        return ResponseEntity.ok(orderDto);
    }

    @ApiOperation("find all order")
    @Authorize(value = ADMINISTRATOR_CUSTOMER_SCOPE)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderDto>> findAllOrders(@RequestParam(defaultValue = "0") final Integer pageNumber,
                                                        @RequestParam(defaultValue = "10") final Integer pageSize,
                                                        final HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderService.findAllOrdersByAccount(pageNumber, pageSize, httpServletRequest));
    }

    @ApiOperation(value = "find specified orders", notes = "find order by order Id, multiple order Id is supported")
    @Authorize(value = ADMINISTRATOR_CUSTOMER_SCOPE)
    @GetMapping(value = "/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderDto>> findOrders(@RequestParam(defaultValue = "0") final Integer pageNumber,
                                                     @RequestParam(defaultValue = "10") final Integer pageSize,
                                                     @RequestParam(defaultValue = "false") final boolean filterDeletedOrder,
                                                     @PathVariable("ids") String ids,
                                                     final HttpServletRequest httpServletRequest) {
        List<String> orderIds = List.of(ids.split(COMMA));
        return ResponseEntity
            .ok(orderService.findOrdersByAccount(pageNumber, pageSize, filterDeletedOrder, orderIds, httpServletRequest));
    }

    @ApiOperation(value = "update order", notes = "update order by order Id")
    @Authorize(value = CUSTOMER_SCOPE)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> updateOrder(@Validated @RequestBody final PostOrderRequest postOrderRequest,
                                                @PathVariable final String id,
                                                final HttpServletRequest httpServletRequest,
                                                final BindingResult bindingResult) {
        if (!bindingResult.getAllErrors().isEmpty()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        OrderDto order = orderService.createOrder(postOrderRequest, id, httpServletRequest);
        return ResponseEntity.ok(order);
    }

    @ApiOperation(value = "delete order", notes = "delete order by order Id, multiple order Id is supported")
    @Authorize(value = CUSTOMER_SCOPE)
    @DeleteMapping(value = "/{ids}")
    public ResponseEntity<List<OrderDto>> deleteOrder(@PathVariable final String ids,
                                                      final HttpServletRequest httpServletRequest) {
        List<String> orderIds = List.of(ids.split(","));
        List<OrderDto> orders = orderService.deleteOrders(orderIds, httpServletRequest);
        return ResponseEntity.ok(orders);
    }

}
