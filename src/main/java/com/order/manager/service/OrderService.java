package com.order.manager.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.order.manager.dto.order.OrderDto;
import com.order.manager.dto.order.PostOrderRequest;
import com.order.manager.enums.AccountType;
import com.order.manager.enums.OrderLineStatus;

import com.order.manager.exception.AuthorizationFailedException;
import com.order.manager.exception.OrderNotFoundException;
import com.order.manager.jdbc.OrderJDBCRepository;
import com.order.manager.jdbc.OrderLineJDBCRepository;
import com.order.manager.mapper.DtoConverter;
import com.order.manager.model.Account;
import com.order.manager.model.Order;
import com.order.manager.repository.AccountRepository;
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
    private JPAQueryFactory         jpaQueryFactory;

    @Autowired
    private OrderRepository         orderRepository;
    @Autowired
    private OrderJDBCRepository     orderJDBCRepository;

    //    @Autowired
    //    private OrderLineRepository orderLineRepository;
    @Autowired
    private OrderLineJDBCRepository orderLineRepository;

    @Autowired
    private CalculateOrder          calculateOrder;

    @Autowired
    private RandomIdGenerator       randomIdGenerator;

    @Autowired
    private DtoConverter            dtoConverter;

    @Autowired
    private AccountRepository       accountRepository;

    public OrderDto createOrder(PostOrderRequest postOrderRequest,
                                HttpServletRequest httpServletRequest) {

        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));

        Order order = postOrderRequest.toOrder(account);
        order.setOrderId(randomIdGenerator.orderIdGenerator());

        calculateOrder.calculateOrderTotalPrice(order);
        orderJDBCRepository.saveAndFlush(order);
        return dtoConverter.convert(order, OrderDto.class);
    }

    public OrderDto updateOrder(PostOrderRequest postOrderRequest, String orderId,
                                HttpServletRequest httpServletRequest) {

        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        Order existingOrder = orderJDBCRepository.findByAccountNameAndOrderId(account.getAccountName(), orderId);
        if (existingOrder == null) {
            throw new OrderNotFoundException(orderId);
        }

        Order newOrder = postOrderRequest.updateOrder(existingOrder);

        //            order.getOrderLines().forEach(orderLine -> {
        //                if (orderLineRepository.findByUuid(orderLine.getUuid()) == null) {
        //                    throw new OrderLineNotFoundException(orderLine.getUuid());
        //                }
        //            });

        calculateOrder.calculateOrderTotalPrice(newOrder);
        orderJDBCRepository.saveAndFlush(newOrder);
        return dtoConverter.convert(newOrder, OrderDto.class);
    }

    public Page<OrderDto> findAllOrdersByAccount(Integer pageNumber, Integer pageSize,
                                                 HttpServletRequest httpServletRequest) {
        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        Pageable pageable = getPageable(pageNumber, pageSize);
        Page<Order> findResult;
        if (account.getAccountType() == AccountType.CUSTOMER) {
            findResult = orderJDBCRepository.findByAccountName(account.getAccountName(), pageable);
        } else {
            findResult = orderJDBCRepository.findAll(pageable);
        }
        return dtoConverter.convert(findResult, OrderDto.class, pageable);//查一下Pageable
    }

    public Page<OrderDto> findOrdersByAccount(Integer pageNumber, Integer pageSize,
                                              boolean filterDeletedOrder, List<String> orderIds,
                                              HttpServletRequest httpServletRequest) {

        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        Pageable pageable = getPageable(pageNumber, pageSize);
        Page<Order> findResult;
        boolean shouldShowAllOrders = shouldShowAllOrders(orderIds);
        if (account.getAccountType() == AccountType.CUSTOMER) {
            if (shouldShowAllOrders) {
                findResult = filterDeletedOrder
                    ? orderJDBCRepository.findByStatusNotAndAccountName(OrderLineStatus.DELETED,
                        account.getAccountName(), pageable)
                    : orderJDBCRepository.findByAccountName(account.getAccountName(), pageable);
                return dtoConverter.convert(findResult, OrderDto.class, pageable);
            }

            findResult = filterDeletedOrder
                ? orderJDBCRepository.findByOrderIdInAndStatusNotAndAccountName(orderIds,
                    OrderLineStatus.DELETED, account.getAccountName(), pageable)
                : orderJDBCRepository.findByOrderIdInAndAccountName(orderIds,
                    account.getAccountName(), pageable);

            //            BooleanBuilder builder = new BooleanBuilder();
            //            builder.and(QOrder.order.orderId.in(orderIds));//可以放到查询的where里面去
            //            JPAQuery<Order> query = jpaQueryFactory.selectFrom(QOrder.order)
            //                                                   .where(QOrder.order.orderId.in(orderIds))
            //                                                   .offset(pageable.getOffset())
            //                                                   .limit(pageable.getPageSize())
            //                                                   .orderBy(QOrder.order.createAt.asc());//先查找
            //            return new PageImpl<>(query.fetch(),pageable,query.fetchCount());
            return dtoConverter.convert(findResult, OrderDto.class, pageable);
        }

        if (shouldShowAllOrders) {
            findResult = filterDeletedOrder
                ? orderJDBCRepository.findByStatusNot(OrderLineStatus.DELETED, pageable)
                : orderJDBCRepository.findAll(pageable);
            return dtoConverter.convert(findResult, OrderDto.class, pageable);
        }

        findResult = filterDeletedOrder
            ? orderJDBCRepository.findByOrderIdInAndStatusNot(orderIds,
                OrderLineStatus.DELETED, pageable)
            : orderJDBCRepository.findByOrderIdIn(orderIds, pageable);
        return dtoConverter.convert(findResult, OrderDto.class, pageable);
    }

    public List<OrderDto> softDeleteOrders(List<String> orderIds,
                                       HttpServletRequest httpServletRequest) {
        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        List<Order> ordersFound = orderIds.stream()
            .map(orderId -> orderJDBCRepository
                .findByAccountNameAndOrderId(account.getAccountName(), orderId))
            .filter(Objects::nonNull).peek(order -> order.setStatus(OrderLineStatus.DELETED))
            .collect(Collectors.toList());
        validateOrderIds(ordersFound, orderIds);
        ordersFound.forEach(order -> orderJDBCRepository.saveAndFlush(order));
        return dtoConverter.convert(ordersFound, OrderDto.class);
    }

    public List<OrderDto> hardDeleteOrdersWithinAccount(List<String> orderIds,
                                                        HttpServletRequest httpServletRequest) {
        Account account = getAccountByToken(httpServletRequest.getHeader("Authorization"));
        List<Order> ordersDeleted = hardDeleteOrders(orderIds, account);
        return dtoConverter.convert(ordersDeleted, OrderDto.class);
    }

    private List<Order> hardDeleteOrders(List<String> orderIds, Account account) {
        List<Order> ordersDeleted = orderIds.stream()
            .map(orderId -> orderJDBCRepository
                .findByAccountNameAndOrderId(account.getAccountName(), orderId))
            .filter(Objects::nonNull).peek(order -> {
                order.getOrderLines()
                    .forEach(orderLine -> orderLineRepository.deleteByUuid(orderLine.getUuid()));
                orderJDBCRepository.deleteByOrderId(order.getOrderId());
            }).collect(Collectors.toList());
        validateOrderIds(ordersDeleted, orderIds);
        return ordersDeleted;
    }

    private void validateOrderIds(List<Order> ordersFound, List<String> orderIds) {
        List<String> orderIdsFund = ordersFound.stream().map(Order::getOrderId)
            .collect(Collectors.toList());
        String ordersNotFound = orderIds.stream().filter(orderId -> !orderIdsFund.contains(orderId))
            .collect(Collectors.joining(","));
        if (ordersNotFound.length() != 0) {
            throw new OrderNotFoundException(ordersNotFound);
        }
    }

    private Pageable getPageable(Integer pageNumber, Integer pageSize) {
        List<Sort.Order> sortProperties = new ArrayList<>();
        Sort.Order sort = new Sort.Order(Sort.Direction.DESC, "createAt");
        sortProperties.add(sort);
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortProperties));
    }

    private Account getAccountByToken(String token) {
        String accountName;
        try {
            accountName = JWT.decode(token).getClaim("accountName").asString();
        } catch (JWTDecodeException j) {
            throw new AuthorizationFailedException("JWT decoding error！");
        }
        Account accountOnDB = accountRepository.findByAccountName(accountName);
        if (accountOnDB == null) {
            throw new AuthorizationFailedException(
                String.format("no token available, login again please"));
        }
        return accountOnDB;
    }

    private boolean shouldShowAllOrders(List<String> orderIds){
        return orderIds.size() == 1 && orderIds.get(0).equals("ALL");
    }
}
