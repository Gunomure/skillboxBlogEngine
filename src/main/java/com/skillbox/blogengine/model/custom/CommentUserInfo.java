package com.skillbox.blogengine.model.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUserInfo {
    private int id;
    private long timestamp;
    private String text;
    private int userId;
    private String userName;
    private String userPhoto;
}
