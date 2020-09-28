package com.trojanmd.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/how-it-works")
    public String howItWorks() {
        return "how-it-works";
    }

    @GetMapping("/membership")
    public String membership() {
        return "membership";
    }

    @GetMapping("/contact-us")
    public String contactUs() {
        return "contact-us";
    }

}
