package com.skillbox.blogengine.config;

import com.skillbox.blogengine.dto.PostStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToPostStatus implements Converter<String, PostStatus> {
    @Override
    public PostStatus convert(String s) {
        return PostStatus.valueOf(s.toUpperCase());
    }
}
