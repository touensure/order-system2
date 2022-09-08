package com.order.manager.jdbc;

import com.order.manager.model.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderLineJDBCRepositoryImpl implements OrderLineJDBCRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public OrderLine findByUuid(String uuid) {
        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where UUID=?";
        return jdbcTemplate.queryForObject(orderLineQuerySql,
            new BeanPropertyRowMapper<>(OrderLine.class), uuid);
    }

    @Override
    public void deleteByUuid(String uuid) {
        String sql = "delete from ORDER_LINE where UUID=?";
        jdbcTemplate.update(sql, uuid);
    }
}
