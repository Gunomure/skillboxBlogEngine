package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.LoggedUserResponse;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final static Logger LOGGER = Logger.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<User> selectAll() {
        return new ArrayList<>(userRepository.findAll());
    }

    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Wrong index: %d", id)));
    }

    public LoggedUserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User " + email + " not found"));
        return map(user);
    }

    public LoggedUserResponse map(User user) {
        LoggedUserResponse userResponse = new LoggedUserResponse();
        userResponse.setResult(true);
        LoggedUserResponse.UserLoggedData userLoggedData = new LoggedUserResponse.UserLoggedData();
        userLoggedData.setId(user.getId());
        userLoggedData.setName(user.getName());
        userLoggedData.setPhoto(user.getPhoto());
        userLoggedData.setEmail(user.getEmail());
        if (user.isModerator()) {
            System.out.println("user is moderator" + user.isModerator());
            int postsNeedToModerate = postRepository.findPostsNeedToModerate();
            userLoggedData.setModeration(true);
            userLoggedData.setModerationCount(postsNeedToModerate);
            userLoggedData.setSettings(true); // true если пользователь является модератором
        }
        userResponse.setUser(userLoggedData);
        return userResponse;
    }
}
