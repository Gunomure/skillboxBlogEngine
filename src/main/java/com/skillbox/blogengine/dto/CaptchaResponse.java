package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CaptchaResponse {

    private String secret;
    private String image;
}
