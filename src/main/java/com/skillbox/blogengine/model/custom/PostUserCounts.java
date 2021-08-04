package com.skillbox.blogengine.model.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
    private int viewCount;
}