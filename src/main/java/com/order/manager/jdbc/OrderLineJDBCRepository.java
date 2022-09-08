package com.order.manager.jdbc;

import com.order.manager.model.OrderLine;

public interface OrderLineJDBCRepository {
    OrderLine findByUuid(String uuid);
    void deleteByUuid(String uuid);
}
