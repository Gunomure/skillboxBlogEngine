package com.skillbox.blogengine.dto;

import com.skillbox.blogengine.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostInfo {
        @Getter
        @Setter
        @AllArgsConstructor
        public static class UserInfo {
            private final int userId;
            private final String userName;
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

    public PostResponse(List<PostWithAdditionalInfo> postsAdditionalInfo) {
        this.count = postsAdditionalInfo.size();
        this.posts = new ArrayList<>();
        for (PostWithAdditionalInfo item : postsAdditionalInfo) {
            this.posts.add(createPostInfo(item));
        }
    }

    private PostInfo createPostInfo(PostWithAdditionalInfo post) {
        return new PostInfo(post.getId(), post.getTimestamp(), new PostInfo.UserInfo(post.getUserId(), post.getUserName()),
                post.getTitle(), post.getAnnounce(), post.getLikeCount(), post.getDislikeCount(), post.getCommentCount(), post.getViewCount());
    }
}
