package com.skillbox.blogengine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToModerationStatus());
        registry.addConverter(new StringToPostStatus());
        registry.addConverter(new StringToModerationStatusRequest());
    }
}
