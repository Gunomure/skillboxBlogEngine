package com.skillbox.blogengine.config;

import com.skillbox.blogengine.model.ModerationStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToModerationStatus implements Converter<String, ModerationStatus> {
    @Override
    public ModerationStatus convert(String s) {
        return ModerationStatus.valueOf(s.toUpperCase());
    }
}
