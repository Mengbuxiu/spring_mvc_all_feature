package com.oukele.spring.controller;

import com.oukele.spring.entity.Person;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(@RequestHeader(value = "Cache-Control", required = false) String header) {
        System.out.println("header = " + header);
        return "index";
    }

    @GetMapping("/person")
    public ResponseEntity<Person> showBook() {
        Person person = new Person("精神小伙", 100);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .eTag("1.1.1") // lastModified is also available
                .body(person);
    }

}
