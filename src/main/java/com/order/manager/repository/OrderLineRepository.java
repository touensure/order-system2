package com.order.manager.repository;


import com.order.manager.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderLineRepository extends JpaRepository<OrderLine,Long> {
    OrderLine findByUuid(String uuid);
    Integer deleteByUuid(String uuid);
}
