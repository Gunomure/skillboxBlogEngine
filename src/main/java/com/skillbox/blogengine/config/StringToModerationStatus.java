package com.skillbox.blogengine.config;

import com.skillbox.blogengine.model.enums.ModerationStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToModerationStatus implements Converter<String, ModerationStatus> {
    @Override
    public ModerationStatus convert(String s) {
        try {
            return ModerationStatus.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
