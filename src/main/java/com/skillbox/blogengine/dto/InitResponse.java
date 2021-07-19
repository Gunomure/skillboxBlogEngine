package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class InitResponse {
    @Value("${blog_engine.title}")
    private String title;
    @Value("${blog_engine.subtitle}")
    private String subtitle;
    @Value("${blog_engine.phone}")
    private String phone;
    @Value("${blog_engine.email}")
    private String email;
    @Value("${blog_engine.copyright}")
    private String copyright;
    @Value("${blog_engine.copyrightFrom}")
    private String copyrightFrom;
}
