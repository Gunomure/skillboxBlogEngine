package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterData {
    // TODO добавить валидацию через аннотации
    @JsonProperty("e_mail")
//    @Email(message = "email is invalid")
    private String email;
//    @Size(min = 1, message = "Password should be more than 6 symbols")
    private String password;
    private String name;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}