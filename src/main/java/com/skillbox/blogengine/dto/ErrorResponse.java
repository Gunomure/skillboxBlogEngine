package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse extends SimpleResponse {
    private boolean result;
    private Map<String, String> errors;

    public ErrorResponse() {
        errors = new HashMap<>();
    }

    public void addError(String errorType, String errorDescription) {
        errors.put(errorType, errorDescription);
    }
}
