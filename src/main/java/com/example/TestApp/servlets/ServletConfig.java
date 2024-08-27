package com.example.TestApp.servlets;

import com.example.TestApp.security.JWTcore;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<SteamLoginServlet> steamAuthServletRegistration() {
        return new ServletRegistrationBean<>(new SteamLoginServlet(),"/myapp/steam-login" );
    }

    private final JWTcore jwTcore;
    public ServletConfig(JWTcore jwTcore)
    {
        this.jwTcore=jwTcore;
    }

    @Bean
    public ServletRegistrationBean<SteamCallBackServlet> steamCallBackServletRegistration() {
        return new ServletRegistrationBean<>(new SteamCallBackServlet(jwTcore), "/myapp/steam-callback");
    }
}