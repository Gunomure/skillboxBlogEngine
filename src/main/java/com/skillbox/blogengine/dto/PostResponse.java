package com.skillbox.blogengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostInfo {
        @Getter
        @Setter
        @AllArgsConstructor
        public static class UserInfo {
            private final int id;
            private final String name;
        }

        private int id;
        private long timestamp;
        private UserInfo user;
        private String title;
        private String announce;
        private long likeCount;
        private long dislikeCount;
        private long commentCount;
        private long viewCount;
    }

    private int count;
    List<PostInfo> posts;
}
