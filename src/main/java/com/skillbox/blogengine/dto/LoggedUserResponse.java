package com.skillbox.blogengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class LoggedUserResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoggedData {
        private int id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;
    }
    private boolean result;
    private UserLoggedData user;
}