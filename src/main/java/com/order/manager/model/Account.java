package com.order.manager.model;

import com.order.manager.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @Column(name = "ACCOUNT_NAME", unique = true, nullable = false)
    @NotNull
    private String accountName;

    @Column(name = "PASSWORD", nullable = false)
    @NotNull
    private String password;

    @Column(name = "EMAIL", nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    @NotNull
    private String email;

    @Column(name = "ACCOUNT_TYPE",nullable = false)
    @Enumerated(EnumType.STRING) //EnumType.ORDINAL则会映射数字顺序编号
    @NotNull
    private AccountType accountType;
}
