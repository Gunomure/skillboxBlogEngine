package com.skillbox.blogengine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
