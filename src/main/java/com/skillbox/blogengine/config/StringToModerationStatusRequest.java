package com.skillbox.blogengine.config;

import com.skillbox.blogengine.dto.enums.ModerationStatusRequest;
import org.springframework.core.convert.converter.Converter;

public class StringToModerationStatusRequest implements Converter<String, ModerationStatusRequest> {
    @Override
    public ModerationStatusRequest convert(String s) {
        try {
            return ModerationStatusRequest.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
