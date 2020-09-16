package com.order.manager.dto.orderLine;

import com.order.manager.utils.myValid.PositiveInteger;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class AddOrderLineRequest {

    @NotNull
    @PositiveInteger(message = "LineNumber should be a positive integer")
    private Integer    lineNumber;

    @Size(max = 255)
    @NotBlank(message = "OrderLine.ProductName must not be blank")
    @NotNull
    private String     productName;

    @PositiveInteger(message = "Quantity should be a positive integer")
    @NotNull
    private String quantity;

    @Positive
    @NotNull
    private BigDecimal unitPrice;
}
