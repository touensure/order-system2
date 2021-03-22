package com.order.manager.repository;

import com.order.manager.enums.OrderLineStatus;
import com.order.manager.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;

public interface OrderRepository extends JpaRepository<Order,Long> {
//[ select 字段列表/* from 表名 where 条件 order by 字段名1 asc/desc, 字段名2 asc/desc,…]用@Query排序
    //LIMIT 3 OFFSET 0;用于分页
    Order findByOrderId(String orderId);

    Order findByAccountNameAndOrderId(String accountName, String orderId);//have a test

    void deleteByOrderId(String orderIds);

    Page<Order> findByOrderIdInAndStatusNotAndAccountName(Collection<String> orderIds, OrderLineStatus status, String accountName, Pageable pageable);
    Page<Order> findByOrderIdInAndStatusNot(Collection<String> orderIds, OrderLineStatus status, Pageable pageable);

    Page<Order> findByOrderIdInAndAccountName(Collection<String> orderIds, String accountName, Pageable pageable);
    Page<Order> findByOrderIdIn(Collection<String> orderIds, Pageable pageable);

    Page<Order> findByStatusNotAndAccountName(OrderLineStatus status, String accountName, Pageable pageable);
    Page<Order> findByStatusNot(OrderLineStatus status, Pageable pageable);

    Page<Order> findByAccountName(String accountName, Pageable pageable);
}
