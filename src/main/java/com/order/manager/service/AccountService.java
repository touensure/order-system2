package com.order.manager.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.order.manager.enums.AccountType;
import com.order.manager.exception.AuthenticationFailedException;
import com.order.manager.exception.RegisterException;
import com.order.manager.model.Account;
import com.order.manager.repository.AccountRepository;
import com.order.manager.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public String login(String accountName, String password, long expireTime){
        Account account = accountRepository.findByAccountName(accountName);
        if(account == null){
            throw new AuthenticationFailedException("Account doesn't exist, check account again");
        }else if(account.getPassword().equals(password)){
            return JwtUtil.generateToken(accountName, password, expireTime);
        }else{
            throw new AuthenticationFailedException("Password doesn't match, check your account again.");
        }
    }

    public Account accountRegister(String accountName,
                                   String password,
                                   String email,
                                   AccountType accountType,
                                   HttpServletRequest httpServletRequest){

        String token = httpServletRequest.getHeader("Authorization");
        if(accountType.equals(AccountType.ADMINISTRATOR)) {
            if (token == null) {
                throw new RegisterException("To register a administrator account, firstly login as an administrator is needed");
            } else {
                String decodedAccountName;
                try {
                    decodedAccountName = JWT.decode(token).getClaim("accountName").asString();
                    Account loginedAccount = accountRepository.findByAccountName(decodedAccountName);
                    if (loginedAccount == null || (!loginedAccount.getAccountType().equals(AccountType.ADMINISTRATOR))) {
                        throw new RegisterException("To register a administrator account, firstly login as an administrator is needed");
                    }
                } catch (JWTDecodeException j) {
                    throw new RegisterException("JWT decoding error！");
                }
            }
        }
        Account account = accountRepository.findByAccountName(accountName);
        if(account == null){
            Account customerAccount = new Account(accountName, password, email, accountType);
            return accountRepository.saveAndFlush(customerAccount);
        }else{
            throw new RegisterException(String.format("Account:%s already exists", accountName));
        }
    }

}
