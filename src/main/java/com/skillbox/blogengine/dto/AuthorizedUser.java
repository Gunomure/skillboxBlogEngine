package com.skillbox.blogengine.dto;

import com.skillbox.blogengine.model.ModerationStatus;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AuthorizedUser extends UserResponse {

    private boolean result;
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}