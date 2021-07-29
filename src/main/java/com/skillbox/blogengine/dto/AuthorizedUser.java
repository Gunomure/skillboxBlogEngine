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

    public AuthorizedUser map(User user) {
        AuthorizedUser userResponse = new AuthorizedUser();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        if (user.isModerator()) {
            System.out.println("user is moderator" + user.isModerator());
            Set<Post> posts = user.getPostsModerated();
            int moderationCount = 0;
            for (Post post : posts) {
                if (post.getModerationStatus() == ModerationStatus.NEW) {
                    moderationCount++;
                }
            }
            userResponse.setModeration(true);
            userResponse.setModerationCount(moderationCount);
            userResponse.setSettings(true); // TODO точно ли так?
        } else {
            userResponse.setModeration(false);
            userResponse.setModerationCount(0);
        }
        return userResponse;
    }
}