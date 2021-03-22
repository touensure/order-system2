package com.order.manager.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.order.manager.enums.AccountType;
import com.order.manager.exception.AuthorizationFailedException;
import com.order.manager.model.Account;
import com.order.manager.repository.AccountRepository;
import com.order.manager.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * jimisun
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        // get token from http request
        String token = httpServletRequest.getHeader("Authorization");

        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        //is there @Authorize
        if (method.isAnnotationPresent(Authorize.class)) {
            Authorize authorize = method.getAnnotation(Authorize.class);
            // Authorization begin
            if (token == null) {
                throw new AuthorizationFailedException("no token available，login please");
            }

            String accountName;
            try {
                accountName = JWT.decode(token).getClaim("accountName").asString();
            } catch (JWTDecodeException j) {
                throw new AuthorizationFailedException("JWT decoding error！");
            }
            Account accountOnDB = accountRepository.findByAccountName(accountName);
            if (accountOnDB == null) {
                throw new AuthorizationFailedException("no token available, login again please");
            }else if(!AccountType.stringToList(authorize.value()).contains(accountOnDB.getAccountType())){
                throw new AuthorizationFailedException("Access to this API denied, your account doesn't have permission!");
            }
            Boolean verify = JwtUtil.isVerify(token, accountOnDB.getAccountName(), accountOnDB.getPassword());
            if (!verify) {
                throw new AuthorizationFailedException("Authorization failed!");
            }
            return true;

        }
        return true;
    }

//    @Override
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//
//    }


}
