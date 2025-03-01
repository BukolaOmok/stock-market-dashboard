package org.bukola.stockmarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")

public class HomeController {
    @GetMapping("/")
    public String hello () {
        return "Hello from Bukola Spring Boot Stock Market Dashboard";
    }
}
