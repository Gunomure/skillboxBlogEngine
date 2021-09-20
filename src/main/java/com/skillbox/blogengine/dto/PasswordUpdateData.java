package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateData {
    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}