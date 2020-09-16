package com.order.manager.dto.orderLine;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.order.manager.model.Order;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderLineDto {

    private String     uuid;

    private Order      order;

    private Integer    lineNumber;

    private String     productName;

    private Long       quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineSubTotal;
}
