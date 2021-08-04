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
}
