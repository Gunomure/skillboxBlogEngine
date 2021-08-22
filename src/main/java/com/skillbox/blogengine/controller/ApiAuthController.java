package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.UserRepository;
import com.skillbox.blogengine.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final static Logger LOGGER = LogManager.getLogger(ApiAuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;

    public ApiAuthController(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginData loginData) {
        Optional<User> byEmail = userRepository.findByEmail("test2@mail.ru");
        LOGGER.info("!!!login user name: {}", byEmail.get().getName());
        LOGGER.info("!!!login user name: {}", byEmail.get().getPassword());

        LOGGER.info("login user name: {}", loginData.getEmail());
        LOGGER.info("login user name: {}", loginData.getPassword());
        Authentication auth = new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String userName = auth.getName();
        LOGGER.info("logged user name: {}", userName);

        return userService.getByEmail(auth.getName());
    }

    @GetMapping("/auth/check")
    private UserResponse checkAuth(Principal principal) {
        if (principal == null) {
            throw new EntityNotFoundException("User not found");
        } else {
            return userService.getByEmail(principal.getName());
        }
//        try {
//            User user = userService.getById(1);
//            AuthorizedUser authorizedUser = new AuthorizedUser();
//            return authorizedUser.map(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        return new NotAuthorizedUser();
    }
}
