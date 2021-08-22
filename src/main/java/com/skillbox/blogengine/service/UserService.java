package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.AuthorizedUser;
import com.skillbox.blogengine.model.ModerationStatus;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final static Logger LOGGER = Logger.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> selectAll() {
        return new ArrayList<>(userRepository.findAll());
    }

    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Wrong index: %d", id)));
    }

    public AuthorizedUser getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(""));
        return map(user);
    }

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
            userResponse.setSettings(true); // true если пользователь является модератором
        }
//        else {
//            userResponse.setModeration(false);
//            userResponse.setModerationCount(0);
//        }
        return userResponse;
    }
}
