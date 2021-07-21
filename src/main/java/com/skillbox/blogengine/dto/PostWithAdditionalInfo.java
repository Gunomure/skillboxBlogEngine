package com.skillbox.blogengine.dto;

import lombok.Data;

@Data
public class PostWithAdditionalInfo {
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