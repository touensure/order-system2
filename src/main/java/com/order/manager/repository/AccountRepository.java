package com.order.manager.repository;

import com.order.manager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByAccountName(String accountName);
}
