package com.skillbox.blogengine.model.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostWithComments {
    private int id;
    private long timestamp;
    private boolean active;
    private int userId;
    private String userName;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
}
