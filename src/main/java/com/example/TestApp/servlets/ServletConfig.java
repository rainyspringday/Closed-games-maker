package com.example.TestApp.servlets;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<SteamLoginServlet> steamAuthServletRegistration() {
        return new ServletRegistrationBean<>(new SteamLoginServlet(),"/myapp/steam-login" );
    }

    @Bean
    public ServletRegistrationBean<SteamCallBackServlet> steamCallBackServletRegistration() {
        return new ServletRegistrationBean<>(new SteamCallBackServlet(), "/myapp/steam-callback");
    }
}