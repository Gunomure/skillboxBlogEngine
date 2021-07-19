package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    final static Logger LOGGER = Logger.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> selectAll() {
        List<User> users = new ArrayList<>();
        try {
            userRepository.findAll().forEach(users::add);
            return users;
        } catch (Exception e) {
            LOGGER.error("Got error while selecting all data from table users", e);
        }
        return users;
    }

    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Wrong index: %d", id)));
    }
}
