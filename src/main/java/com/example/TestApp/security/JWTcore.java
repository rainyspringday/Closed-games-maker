package com.example.TestApp.security;


import com.vaadin.flow.server.VaadinService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JWTcore {

    private SecretKey getSigningKey() {
        String secret = "your-64-characters-or-more-secret-key-goes-here-abcd1234efgh5678ijkl90mn";
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }


    public String generateToken(String name) {
        String role="ROLE_USER";
        long lifetime =  10 * 60 * 1000;
        return Jwts.builder()
                .claim("name",name)
                .claim("auth", role)
                .issuedAt(new Date()).expiration(new Date(new Date().getTime() + lifetime))
                .signWith(getSigningKey())
                .compact();
    }

    public String[] getAuth(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                ;
        String[] ver = new String[2];
        ver[0]= (String) claims.get("name");
        ver[1]= (String) claims.get("auth");
        return ver;
    }

    public String getTokenFromCookie() {
        HttpServletRequest request = (HttpServletRequest) VaadinService.getCurrentRequest();


        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return "";
    }
}

