package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RegisterErrorResponse extends RegisterResponse {
    private boolean result;
    private Map<String, String> errors;

    public RegisterErrorResponse() {
        errors = new HashMap<>();
    }

    public void addError(String errorType, String errorDescription) {
        errors.put(errorType, errorDescription);
    }
}
