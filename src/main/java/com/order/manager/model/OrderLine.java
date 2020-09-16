package com.order.manager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.order.manager.utils.myValid.PositiveInteger;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_line")
public class OrderLine implements Serializable {
    @Id
    @Column(name ="UUID")
    @NotNull
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    @JsonBackReference("orderlines")
    @NotNull
    private Order order;

    @Column(name = "LINE_NUMBER",nullable = false)
    @NotNull
    @PositiveInteger
    private Integer lineNumber;

    @Column(name = "PRODUCT_NAME",nullable = false, updatable = false)
    @Size(max = 255)
    @NotBlank(message = "OrderLine.ProductName must not be blank")
    @NotNull
    private String productName;

    @Column(name = "QUANTITY",nullable = false)
    @PositiveInteger
    @NotNull
    private Long quantity;

    @Column(name = "UNIT_PRICE",nullable = false, precision = 16, scale = 6)
    @Positive
    @NotNull
    private BigDecimal unitPrice;

    @Column(name = "LINE_SUB_TOTAL", precision = 16, scale = 6)
    @NotNull
    private BigDecimal lineSubTotal;
}
