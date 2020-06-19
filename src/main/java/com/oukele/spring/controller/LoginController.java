package com.oukele.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(@RequestHeader(value = "Cache-Control", required = false) String header) {
        System.out.println("header = " + header);
        return "index";
    }

}
