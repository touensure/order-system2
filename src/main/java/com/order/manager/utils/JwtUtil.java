package com.order.manager.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    public static String generateToken(String accountName, String password, long expireTime){
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(password),
                                    SignatureAlgorithm.HS256.getJcaName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountName", accountName);
        claims.put("password", password);

        //set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                                 .setIssuedAt(now)
                                 .setIssuer("yinxiang.deng@sap.com")
                                 .setClaims(claims)
                                 .setExpiration(new Date(nowMillis + expireTime))
                                 .signWith(signatureAlgorithm, key);//The JWT signature algorithm we will be using to sign the token, password used ad secret key
        return builder.compact();
    }

    public static Claims parseJWT(String token, String password) {

        //the signature secret should be the same as when generating the token
        Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(password),
                                    SignatureAlgorithm.HS256.getJcaName());

        //DefaultJwtParser
        Claims claims = Jwts.parser()
                            .setSigningKey(key)//set signature secret
                            .parseClaimsJws(token).getBody();
        return claims;
    }

    public static Boolean isVerify(String token, String accountName, String password) {

        //the signature secret should be the same as when generating the token
        Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(password),
                                    SignatureAlgorithm.HS256.getJcaName());

        //DefaultJwtParser
        Claims claims = Jwts.parser()
                            .setSigningKey(key)
                            .parseClaimsJws(token).getBody();

        return claims.get("accountName").equals(accountName);
    }
}
