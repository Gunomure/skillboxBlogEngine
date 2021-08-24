package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedUserResponse {

    private boolean result;
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}