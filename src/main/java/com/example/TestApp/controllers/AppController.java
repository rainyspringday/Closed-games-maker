package com.example.TestApp.controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/myapp")
public class AppController {
    @RequestMapping("/secure-endpoint")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String secureEndpoint() {

        return "wow";
    }

}


