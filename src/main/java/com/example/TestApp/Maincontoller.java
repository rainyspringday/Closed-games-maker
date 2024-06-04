package com.example.TestApp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Maincontoller {
	    @GetMapping("/hello")
	    public String sayHello() {
	        return "Hello, World!";
	        }
}

