package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.controller.exception.UserNotAuthorizedException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.service.CaptchaService;
import com.skillbox.blogengine.service.EmailSender;
import com.skillbox.blogengine.service.AuthService;
import com.skillbox.blogengine.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final static Logger LOGGER = LogManager.getLogger(ApiAuthController.class);

    private final UserService userService;
    private final CaptchaService captchaService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public ApiAuthController(UserService userService, CaptchaService captchaService, AuthService authService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.captchaService = captchaService;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public LoggedUserResponse login(@RequestBody LoginData loginData) {
        LOGGER.info("Login user {}", loginData.getEmail());
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword()));
            if (auth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                return userService.getByEmail(auth.getName());
            }
        } catch (AuthenticationException e) {
            LOGGER.error("Can not authorize user " + loginData.getEmail());
        }
        return new LoggedUserResponse();
    }

    @GetMapping("/check")
    public LoggedUserResponse checkAuth(Principal principal) {
        if (principal == null) {
            throw new UserNotAuthorizedException("User not found");
        } else {
            return userService.getByEmail(principal.getName());
        }
    }

    @GetMapping("/logout")
    public Map<String, Boolean> logout(Principal principal) {
        if (principal != null) {
            SecurityContextHolder.clearContext();
        }
        Map<String, Boolean> result = new HashMap<>();
        result.put("result", true);

        return result;
    }

    @GetMapping("/captcha")
    public CaptchaResponse getCaptcha() {
        captchaService.deleteExpiredCaptcha();
        return captchaService.createAndSaveCaptcha();
    }

    @PostMapping("/register")
    public SimpleResponse postRegister(@RequestBody UserRegisterData userRegisterData) {
        return authService.registerUser(userRegisterData);
    }

    @PostMapping("/restore")
    public SimpleResponse restorePassword(@RequestBody RestorePasswordData restoreData) {
        LOGGER.info("Send email to {}", restoreData.getEmail());
        authService.sendRestoreEmail(restoreData.getEmail());
        return new SimpleResponse(true);
    }
}
