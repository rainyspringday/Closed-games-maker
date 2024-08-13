package com.example.TestApp.security;


import com.example.TestApp.MyUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Component
public class JWTcore {
    private String secret = "your-64-characters-or-more-secret-key-goes-here-abcd1234efgh5678ijkl90mn";
    private long lifetime = 10 * 60 * 60 * 10000;

    private Key getSigningKey() {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }


    public String generateToken(Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return Jwts.builder().subject(userDetails.getUsername())
                .claim("roles", authentication.getAuthorities())
                .issuedAt(new Date()).expiration(new Date(new Date().getTime() + lifetime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }



}

