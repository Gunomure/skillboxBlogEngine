package com.skillbox.blogengine.model.custom;

import lombok.Data;

@Data
public class PostUserCounts {
    private int id;
    private long timestamp;
    private int userId;
    private String userName;
    private String title;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private long viewCount;
}