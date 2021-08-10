package com.skillbox.blogengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostByIdResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Comment {
        private int id;
        private long timestamp;
        private String text;
        private CommentUser user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CommentUser {
        private int id;
        private String name;
        private String photo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostUser {
        private int id;
        private String name;
    }

    private int id;
    private long timestamp;
    private boolean active;
    private PostUser user;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private List<Comment> comments;
    private List<String> tags;
}