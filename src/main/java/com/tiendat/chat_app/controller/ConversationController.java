package com.tiendat.chat_app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversationController {
    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('USER')")
    public String sayHello() {
        return "Hello World";
    }
}
