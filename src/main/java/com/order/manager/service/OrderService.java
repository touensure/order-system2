package com.order.manager.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.order.manager.dto.order.OrderDto;
import com.order.manager.dto.order.PostOrderRequest;
import com.order.manager.enums.AccountType;
import com.order.manager.enums.OrderLineStatus;

import com.order.manager.exception.AuthorizationFailedException;
import com.order.manager.exception.OrderNotFoundException;
import com.order.manager.mapper.DtoConverter;
import com.order.manager.model.Account;
import com.order.manager.model.Order;
import com.order.manager.repository.AccountRepository;
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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private AccountRepository   accountRepository;

    public OrderDto createOrder(PostOrderRequest postOrderRequest,
                                String orderId,
                                HttpServletRequest httpServletRequest) {

        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));

        Order order = postOrderRequest.toOrder(account);
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

    public Page<OrderDto> findAllOrdersByAccount(Integer pageNumber, Integer pageSize, HttpServletRequest httpServletRequest) {
        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        Pageable pageable = getPageable(pageNumber, pageSize);
        Page<Order> findResult;
        if(account.getAccountType() == AccountType.CUSTOMER) {
            findResult = orderRepository.findByAccountName(account.getAccountName(), pageable);
        }else{
            findResult = orderRepository.findAll(pageable);
        }
        return dtoConverter.convert(findResult, OrderDto.class, pageable);//查一下Pageable
    }

    public Page<OrderDto> findOrdersByAccount(Integer pageNumber, Integer pageSize,
                                              boolean filterDeletedOrder, List<String> orderIds,
                                              HttpServletRequest httpServletRequest) {

        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        Pageable pageable = getPageable(pageNumber, pageSize);
        Page<Order> findResult;
        if(account.getAccountType() == AccountType.CUSTOMER) {
            if (orderIds.size() == 0) {
                findResult = filterDeletedOrder ? orderRepository.findByStatusNotAndAccountName(
                                                                                OrderLineStatus.DELETED,
                                                                                account.getAccountName(),
                                                                                pageable)
                                                : orderRepository.findByAccountName(account.getAccountName(), pageable);
            } else {
                findResult = filterDeletedOrder ? orderRepository.findByOrderIdInAndStatusNotAndAccountName(
                                                                                orderIds,
                                                                                OrderLineStatus.DELETED,
                                                                                account.getAccountName(),
                                                                                pageable)
                                                : orderRepository.findByOrderIdInAndAccountName(orderIds,
                                                                                                account.getAccountName(),
                                                                                                pageable);
                validateOrderIds(findResult.getContent(), orderIds);

                //            BooleanBuilder builder = new BooleanBuilder();
                //            builder.and(QOrder.order.orderId.in(orderIds));//可以放到查询的where里面去
                //            JPAQuery<Order> query = jpaQueryFactory.selectFrom(QOrder.order)
                //                                                   .where(QOrder.order.orderId.in(orderIds))
                //                                                   .offset(pageable.getOffset())
                //                                                   .limit(pageable.getPageSize())
                //                                                   .orderBy(QOrder.order.createAt.asc());//先查找
                //            return new PageImpl<>(query.fetch(),pageable,query.fetchCount());
            }
        }else{
            if(orderIds.size() == 0){
                findResult = filterDeletedOrder ? orderRepository.findByStatusNot(OrderLineStatus.DELETED, pageable)
                                                : orderRepository.findAll(pageable);
            }else{
                findResult = filterDeletedOrder ? orderRepository.findByOrderIdInAndStatusNot(
                                                                                    orderIds,
                                                                                    OrderLineStatus.DELETED,
                                                                                    pageable)
                                                : orderRepository.findByOrderIdIn(orderIds, pageable);
                validateOrderIds(findResult.getContent(), orderIds);
            }
        }

        return dtoConverter.convert(findResult, OrderDto.class, pageable);
    }

    public List<OrderDto> deleteOrders(List<String> orderIds,
                                       HttpServletRequest httpServletRequest) {
        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        List<Order> ordersFound = orderIds.stream()
            .map(orderId -> orderRepository.findByAccountNameAndOrderId(account.getAccountName(),orderId))
            .filter(Objects::nonNull)
            .peek(order -> order.setStatus(OrderLineStatus.DELETED)).collect(Collectors.toList());
        validateOrderIds(ordersFound, orderIds);
        ordersFound.forEach(order -> orderRepository.saveAndFlush(order));
        return dtoConverter.convert(ordersFound, OrderDto.class);
    }

    private void validateOrderIds(List<Order> ordersFound, List<String> orderIds){
        List<String> orderIdsFund = ordersFound.stream().map(Order::getOrderId).collect(Collectors.toList());
        String ordersNotFound = orderIds.stream().filter(orderId -> !orderIdsFund.contains(orderId)).collect(Collectors.joining(","));
        if(ordersNotFound.length() != 0){
            throw new OrderNotFoundException(ordersNotFound);
        }
    }

    private Pageable getPageable(Integer pageNumber, Integer pageSize) {
        List<Sort.Order> sortProperties = new ArrayList<>();
        Sort.Order sort = new Sort.Order(Sort.Direction.DESC, "createAt");
        sortProperties.add(sort);
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortProperties));
    }

    private Account getAccountByToken(String token){
        String accountName;
        try {
            accountName = JWT.decode(token).getClaim("accountName").asString();
        } catch (JWTDecodeException j) {
            throw new AuthorizationFailedException("JWT decoding error！");
        }
        Account accountOnDB = accountRepository.findByAccountName(accountName);
        if (accountOnDB == null) {
            throw new AuthorizationFailedException(String.format("no token available, login again please"));
        }
        return accountOnDB;
    }
}
