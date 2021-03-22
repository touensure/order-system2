package com.order.manager.enums;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.order.manager.constant.Constant.COMMA;

public enum AccountType implements Serializable {
    CUSTOMER,
    ADMINISTRATOR;

    public static List<AccountType> stringToList(String authorizeInfo){
         return Arrays.stream(authorizeInfo.split(COMMA))
                      .map(AccountType::valueOf)
                      .collect(Collectors.toList());
    }
}
