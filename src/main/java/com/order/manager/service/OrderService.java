package com.order.manager.service;

import com.order.manager.dto.order.OrderDto;
import com.order.manager.dto.order.PostOrderRequest;
import com.order.manager.enums.OrderLineStatus;
import com.order.manager.exception.OrderLineNotFoundException;
import com.order.manager.exception.OrderNotFoundException;
import com.order.manager.mapper.DtoConverter;
import com.order.manager.model.Order;
import com.order.manager.repository.OrderLineRepository;
import com.order.manager.repository.OrderRepository;
import com.order.manager.utils.CalculateOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Component;
import com.order.manager.utils.RandomIdGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.util.ListUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional
public class OrderService {

    @Bean
    @Autowired
    public JPAQueryFactory jpaQuery(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    @Autowired
    private JPAQueryFactory     jpaQueryFactory;

    @Autowired
    private OrderRepository     orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private CalculateOrder      calculateOrder;

    @Autowired
    private RandomIdGenerator   randomIdGenerator;

    @Autowired
    private DtoConverter        dtoConverter;

    public OrderDto creatOrder(PostOrderRequest postOrderRequest, String orderId) {

        Order order = postOrderRequest.toOrder();
        if (orderId == null) {
            order.setOrderId(randomIdGenerator.orderIdGenerator());
        } else {
            Order existingOrder = orderRepository.findByOrderId(orderId);
            if (existingOrder == null) {
                throw new OrderNotFoundException(orderId);
            }else{
                orderRepository.deleteByOrderId(existingOrder.getOrderId());
                existingOrder.getOrderLines().forEach(orderLine -> orderLineRepository.deleteByUuid(orderLine.getUuid()));
            }

//            order.getOrderLines().forEach(orderLine -> {
//                if (orderLineRepository.findByUuid(orderLine.getUuid()) == null) {
//                    throw new OrderLineNotFoundException(orderLine.getUuid());
//                }
//            });
            order.setOrderId(orderId);
        }

        calculateOrder.calculateOrderTotalPrice(order);
        orderRepository.saveAndFlush(order);
        order.getOrderLines().forEach(orderLine -> orderLineRepository.saveAndFlush(orderLine));
        return dtoConverter.convert(order, OrderDto.class);
    }

    public Page<OrderDto> findAllOrders(Integer pageNumber, Integer pageSize) {
        Pageable pageable = getPageable(pageNumber, pageSize);
        Page<Order> findResult = orderRepository.findAll(pageable);
        return dtoConverter.convert(findResult, OrderDto.class, pageable);//查一下Pageable
    }

    public Page<OrderDto> findOrders(Integer pageNumber, Integer pageSize,
                                     boolean filterCancelledOrder, List<String> orderIds) {

        if (ListUtils.isEmpty(orderIds)) {
            Pageable pageable = getPageable(pageNumber, pageSize);
            Page<Order> findResult = filterCancelledOrder?orderRepository.findByStatusNot(OrderLineStatus.CANCELLED,pageable)
                                                         :orderRepository.findAll(pageable);
//            Page<Order> findResult = orderRepository.findAll(pageable);
            return dtoConverter.convert(findResult, OrderDto.class, pageable);
        } else {
            Pageable pageable = getPageable(pageNumber, pageSize);
            Page<Order> findResult = filterCancelledOrder
                ? orderRepository.findByOrderIdInAndStatusNot(orderIds, OrderLineStatus.CANCELLED,
                    pageable)
                : orderRepository.findByOrderIdIn(orderIds, pageable);
            if(findResult.getContent().isEmpty()){
                throw new OrderNotFoundException(String.join(",",orderIds));
            }
            return dtoConverter.convert(findResult, OrderDto.class, pageable);

            //            BooleanBuilder builder = new BooleanBuilder();
            //            builder.and(QOrder.order.orderId.in(orderIds));//可以放到查询的where里面去
            //            JPAQuery<Order> query = jpaQueryFactory.selectFrom(QOrder.order)
            //                                                   .where(QOrder.order.orderId.in(orderIds))
            //                                                   .offset(pageable.getOffset())
            //                                                   .limit(pageable.getPageSize())
            //                                                   .orderBy(QOrder.order.createAt.asc());//先查找
            //            return new PageImpl<>(query.fetch(),pageable,query.fetchCount());
        }
    }

    public List<OrderDto> deleteOrders(List<String> orderIds) throws OrderNotFoundException {

        List<Order> orders = new ArrayList<>();
        orderIds.forEach(orderId -> {
            Order order = orderRepository.findByOrderId(orderId);
            if (order != null) {
                orders.add(order);
                orderRepository.deleteByOrderId(orderId);
            } else {
                throw new OrderNotFoundException(orderId);
            }
        });
        return dtoConverter.convert(orders, OrderDto.class);
    }

    public List<OrderDto> cancelOrders(List<String> orderIds) {
        List<Order> orders = orderIds.stream()
            .map(orderId -> orderRepository.findByOrderId(orderId)).filter(Objects::nonNull)
            .peek(order -> order.setStatus(OrderLineStatus.CANCELLED)).collect(Collectors.toList());
        orders.forEach(order -> orderRepository.saveAndFlush(order));
        return dtoConverter.convert(orders, OrderDto.class);
    }

    private Pageable getPageable(Integer pageNumber, Integer pageSize) {
        List<Sort.Order> sortProperties = new ArrayList<>();
        Sort.Order sort = new Sort.Order(Sort.Direction.DESC, "createAt");
        sortProperties.add(sort);
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortProperties));
    }
}
