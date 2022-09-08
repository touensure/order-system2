package com.order.manager.jdbc;

import com.order.manager.enums.OrderLineStatus;
import com.order.manager.model.Order;
import com.order.manager.model.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class OrderJDBCRepositoryImpl implements OrderJDBCRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Page<Order> findAll(Pageable pageable) {
        String orderQuerySql = "SELECT * FROM ORDER_TABLE";
        //queryForList(sql, new RowMapper(), ...) also available
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class));

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Order findByOrderId(String orderId) {    //only for merchant
        String orderQuerySql = "SELECT * FROM ORDER_TABLE where ORDER_ID=?";
        List<Order> queryResult = jdbcTemplate.query(orderQuerySql,
            new BeanPropertyRowMapper<>(Order.class), orderId);
        if(ObjectUtils.isEmpty(queryResult)){
            return null;
        }
        Order order =queryResult.get(0);

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                new BeanPropertyRowMapper<>(OrderLine.class), orderId);
        orderLines.forEach(ol -> ol.setOrder(order));
        order.setOrderLines(orderLines);
        return order;
    }

    @Override
    public Order findByAccountNameAndOrderId(String accountName, String orderId) {
        String orderQuerySql = "select * from ORDER_TABLE o inner join ORDER_LINE l on o.order_id=l.order_id where o.ORDER_ID=? and o.ACCOUNT_NAME=?";
        List<Order> queryResult =  jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class), orderId,
            accountName);
        if(ObjectUtils.isEmpty(queryResult)){
            return null;
        }
        Order order = queryResult.get(0);

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                new BeanPropertyRowMapper<>(OrderLine.class), orderId);
        orderLines.forEach(ol -> ol.setOrder(order));
        order.setOrderLines(orderLines);
        return order;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        String sql = "delete from ORDER_TABLE where ORDER_ID=?";
        jdbcTemplate.update(sql, orderId);
    }

    @Override
    public Page<Order> findByOrderIdInAndStatusNotAndAccountName(Collection<String> orderIds,
                                                                 OrderLineStatus status,
                                                                 String accountName,
                                                                 Pageable pageable) {
        String orderQuerySql = passingACollectionParameterToInClause("SELECT * FROM ORDER_TABLE where ORDER_ID in (%s) and STATUS!=? and ACCOUNT_NAME=?", orderIds);
        List<String> aggregatedInfo = new ArrayList<>(orderIds);
        aggregatedInfo.add(status.toString());
        aggregatedInfo.add(accountName);
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
                aggregatedInfo.toArray());

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByOrderIdInAndStatusNot(Collection<String> orderIds,
                                                   OrderLineStatus status, Pageable pageable) {
        String orderQuerySql = passingACollectionParameterToInClause("SELECT * FROM ORDER_TABLE where ORDER_ID in (%s) and STATUS!=?", orderIds);
        List<String> aggregatedInfo = new ArrayList<>(orderIds);
        aggregatedInfo.add(status.toString());
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
                aggregatedInfo.toArray());

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByOrderIdInAndAccountName(Collection<String> orderIds,
                                                     String accountName, Pageable pageable) {
        String orderQuerySql = passingACollectionParameterToInClause("SELECT * FROM ORDER_TABLE where ORDER_ID in (%s) and ACCOUNT_NAME=?", orderIds);
        List<String> aggregatedInfo = new ArrayList<>(orderIds);
        aggregatedInfo.add(accountName);
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
                aggregatedInfo.toArray());

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByOrderIdIn(Collection<String> orderIds, Pageable pageable) {
        String orderQuerySql = passingACollectionParameterToInClause("SELECT * FROM ORDER_TABLE where ORDER_ID in (%s)", orderIds);
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
            orderIds.toArray());

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByStatusNotAndAccountName(OrderLineStatus status, String accountName,
                                                     Pageable pageable) {
        String orderQuerySql = "SELECT * FROM ORDER_TABLE where STATUS!=? and ACCOUNT_NAME=?";
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
            status.toString(), accountName);

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByStatusNot(OrderLineStatus status, Pageable pageable) {
        String orderQuerySql = "SELECT * FROM ORDER_TABLE where STATUS!=?";
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
            status.toString());

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findByAccountName(String accountName, Pageable pageable) {
        String orderQuerySql = "SELECT * FROM ORDER_TABLE where ACCOUNT_NAME=?";
        List<Order> orders = jdbcTemplate.query(orderQuerySql, new BeanPropertyRowMapper<>(Order.class),
            accountName);

        String orderLineQuerySql = "SELECT * FROM ORDER_LINE where ORDER_ID=?";
        orders.forEach(order -> {
            List<OrderLine> orderLines = jdbcTemplate.query(orderLineQuerySql,
                    new BeanPropertyRowMapper<>(OrderLine.class), order.getOrderId());
            orderLines.forEach(ol -> ol.setOrder(order));
            order.setOrderLines(orderLines);});
        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public void saveAndFlush(Order order) {
        //clear existing Order
        Order existingOrder = this.findByOrderId(order.getOrderId());
        if(Objects.nonNull(existingOrder)){
            String sql = "delete from ORDER_LINE where UUID=?";
            existingOrder.getOrderLines().forEach(ol -> jdbcTemplate.update(sql, ol.getUuid()));
            deleteByOrderId(existingOrder.getOrderId());
        }

        //insert new Order
        String saveOrderSql = "INSERT INTO ORDER_TABLE VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveOrderSql, order.getOrderId(), order.getCreateAt(),
            order.getTotalPrice(), order.getStatus().toString(), order.getAccountName(),
            order.getCustomerEmail());

        //insert new orderLines
        String saveOrderLineSql = "INSERT INTO ORDER_LINE VALUES (?, ?, ?, ?, ?, ?, ?)";
        order.getOrderLines()
            .forEach(ol -> jdbcTemplate.update(saveOrderLineSql, ol.getUuid(),
                ol.getOrder().getOrderId(), ol.getLineNumber(), ol.getProductName(),
                ol.getQuantity(), ol.getUnitPrice(), ol.getLineSubTotal()));
    }

    private String passingACollectionParameterToInClause(String rawSql, Collection<String> orderIds){
        String inSql = String.join(",", Collections.nCopies(orderIds.size(), "?"));
        return String.format(rawSql, inSql);
    }
}
