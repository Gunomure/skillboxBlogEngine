package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;

import java.util.ArrayList;
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

    @JsonIgnore
    private static final int ANNOUNCE_MAX_LENGTH = 150;

    private int count;
    List<PostInfo> posts;

    public static PostResponse map(List<PostUserCounts> postsAdditionalInfo) {
        PostResponse response = new PostResponse();
        response.setCount(postsAdditionalInfo.size());

        List<PostInfo> posts = new ArrayList<>();
        for (PostUserCounts item : postsAdditionalInfo) {
            posts.add(createPostInfo(item));
        }
        response.setPosts(posts);
        return response;
    }

    private static PostInfo createPostInfo(PostUserCounts post) {
        // так проще всего удалить html тэги
        String announceWithoutTags = Jsoup.parse(post.getAnnounce()).text();
        if (announceWithoutTags.length() > ANNOUNCE_MAX_LENGTH) {
            announceWithoutTags = announceWithoutTags.substring(0, ANNOUNCE_MAX_LENGTH);
        }
        // TODO не понятно, добавлять многоточие всегда или если длина >150
        announceWithoutTags += "...";

        return new PostInfo(post.getId(), post.getTimestamp(), new PostInfo.UserInfo(post.getUserId(), post.getUserName()),
                post.getTitle(), announceWithoutTags, post.getLikeCount(), post.getDislikeCount(), post.getCommentCount(), post.getViewCount());
    }
}
