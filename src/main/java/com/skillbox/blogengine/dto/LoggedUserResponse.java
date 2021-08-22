package com.skillbox.blogengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedUserResponse extends LoginResponse {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class LoggedUserData {
        private int id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private long moderationCount;
        private boolean settings;
    }

    private LoggedUserData user;
}