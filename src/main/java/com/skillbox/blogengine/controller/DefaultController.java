package com.skillbox.blogengine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{[path:[^\\\\.]*}")
    public String redirectToIndex() {
        System.out.println("redirect");
        return "forward:/";
    }
}
