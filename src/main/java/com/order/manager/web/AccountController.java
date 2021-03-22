package com.order.manager.web;

import com.order.manager.enums.AccountType;
import com.order.manager.model.Account;
import com.order.manager.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Account Operations", tags = "Accounts")
@RestController
@RequestMapping("/accounts")
@Transactional
public class AccountController {
    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "account login", notes = "an administrator or customer account can login")
    @PostMapping(value = "/login")
    public String longin(@RequestParam final String accountName,
                         @RequestParam final String password){
        return accountService.login(accountName, password, 5*60*60*1000);//5 hours
    }

    @ApiOperation(value = "Account registration", notes ="firstly login as a existing administrator is needed if registration account type is administrator")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Account administratorRegister(@RequestParam final String accountName,
                                         @RequestParam final String password,
                                         @RequestParam final String email,
                                         @RequestParam final AccountType accountType,
                                         HttpServletRequest httpServletRequest){
        return accountService.accountRegister(accountName, password, email, accountType, httpServletRequest);
    }
}
