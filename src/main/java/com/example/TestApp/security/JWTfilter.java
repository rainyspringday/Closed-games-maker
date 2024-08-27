package com.example.TestApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JWTfilter extends OncePerRequestFilter {
    private final JWTcore jwTcore;

    public JWTfilter(JWTcore jwTcore) {
        this.jwTcore = jwTcore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            String token = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if(token!=null)
            {

                SimpleGrantedAuthority roleUserAuthority = new SimpleGrantedAuthority(jwTcore.getAuth(token)[1]);
                List<SimpleGrantedAuthority> updatedAuthorities = Collections.singletonList(roleUserAuthority);
                MyUserDetails userDetails = new MyUserDetails(jwTcore.getAuth(token)[0], "", updatedAuthorities);
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        updatedAuthorities
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }


        }
        catch (Exception ignored)
        {

        }
        filterChain.doFilter(request,response);
    }



}
