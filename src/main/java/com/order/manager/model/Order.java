package com.order.manager.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.order.manager.enums.OrderLineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ORDER_TABLE")
@Entity
public class Order implements Serializable {
    @Id
    @Column(name = "ORDER_ID", unique = true, nullable = false, updatable = false) //name,nullable, unique,updatable,length
    @NotNull
    private String          orderId;

    @Column(name = "CREATE_AT", nullable = false, updatable = false)
    @NotNull
    private Date            createAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("orderlines")
    @Size(min = 1)
    @NotNull
    private List<OrderLine> orderLines;

    @Column(name = "TOTAL_PRICE", precision = 16, scale = 6)
    @NotNull
    private BigDecimal      totalPrice;

    @Enumerated(EnumType.STRING) //EnumType.ORDINAL则会映射数字顺序编号
    @Column(name = "STATUS", nullable = false)
    @NotNull
    private OrderLineStatus status;

    @Column(name = "ACCOUNT_NAME", unique = true, nullable = false)
    @NotNull
    private String          accountName;

    @Column(name = "CUSTOMER_EMAIL", nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    @NotNull
    private String          customerEmail;

    public OrderLine lineOfnumber(Integer lineNumber) {
        return this.getOrderLines().stream().filter(ol -> ol.getLineNumber().equals(lineNumber))
            .findFirst().orElse(null);
    }
}
