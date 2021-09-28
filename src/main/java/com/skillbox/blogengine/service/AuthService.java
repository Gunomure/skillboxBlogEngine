package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.controller.exception.SimpleException;
import com.skillbox.blogengine.config.SecurityConfig;
import com.skillbox.blogengine.dto.ErrorResponse;
import com.skillbox.blogengine.dto.PasswordUpdateData;
import com.skillbox.blogengine.dto.SimpleResponse;
import com.skillbox.blogengine.dto.UserRegisterData;
import com.skillbox.blogengine.model.CaptchaCode;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.CaptchaRepository;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final SecurityConfig securityConfig;
    private final EmailSender emailSender;
    private final PasswordEncoder encoder;

    @Value("${blog_engine.additional.websiteHost}")
    private String WEBSITE_HOST;
    private final static String EMAIL_TEMPLATE = "<a href=\"http://%s:8080/login/change-password/%s\">Click to restore your password</a>";

    public AuthService(UserRepository userRepository, CaptchaRepository captchaRepository, SecurityConfig securityConfig, EmailSender emailSender, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.securityConfig = securityConfig;
        this.emailSender = emailSender;
        this.encoder = encoder;
    }

    public SimpleResponse registerUser(UserRegisterData user) {
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        CaptchaCode captchaCodeBySecretCode = captchaRepository.findCaptchaCodeBySecretCode(user.getCaptchaSecret());
        if (!userByEmail.isPresent()
                && captchaCodeBySecretCode != null
                && captchaCodeBySecretCode.getCode().equals(user.getCaptcha())
                && user.getPassword().length() >= 6
                && new EmailValidator().isValid(user.getEmail(), null)) {
            LOGGER.info("User's registration data is valid. User will be saved in the DB");
            User newUser = new User();
            newUser.setRegTime(LocalDateTime.now());
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(newUser);
            return new SimpleResponse(true);
        } else {
            LOGGER.info("User's registration data is invalid. Return error");
            ErrorResponse registerResponse = new ErrorResponse();
            registerResponse.setResult(false);
            if (userByEmail.isPresent()) {
                registerResponse.addError("email", "Этот e-mail уже зарегистрирован");
            }
            if (captchaCodeBySecretCode == null || !captchaCodeBySecretCode.getCode().equals(user.getCaptcha())) {
                registerResponse.addError("captcha", "Код с картинки введён неверно");
            }
            if (user.getPassword().length() < 6) {
                registerResponse.addError("password", "Пароль короче 6-ти символов");
            }
            if (!(new EmailValidator().isValid(user.getEmail(), null))) {
                registerResponse.addError("email", "e-mail введен не верно");
            }
            return registerResponse;
        }
    }

    public void sendRestoreEmail(String email) {
        LOGGER.info("fiend by {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new SimpleException(String.format("User %s not found", email)));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setCode(uuid);
        userRepository.save(user);
        emailSender.sendSimpleMessage(email, "Restore password", String.format(EMAIL_TEMPLATE, WEBSITE_HOST, uuid));
    }

    public void updatePassword(PasswordUpdateData passwordUpdateData) {
        Optional<User> user = userRepository.findByCode(passwordUpdateData.getCode());
        CaptchaCode captchaCode = captchaRepository.findCaptchaCodeBySecretCode(passwordUpdateData.getCaptchaSecret());

        BadRequestException exception = new BadRequestException("Error while checking data for restore password");
        if (!user.isPresent() || !user.get().getCode().equals(passwordUpdateData.getCode())) {
            exception.addErrorDescription("code", "Ссылка для восстановления пароля устарела." +
                            "<a href= \"/login/restore-password\">Запросить ссылку снова</a>");
        }
        if (passwordUpdateData.getPassword().length() < 6) {
            exception.addErrorDescription("password", "Пароль короче 6-ти символов");
        }
        if (!captchaCode.getCode().equals(passwordUpdateData.getCaptcha())) {
            exception.addErrorDescription("captcha", "Код с картинки введён неверно");
        }
        if (!exception.getErrorsDescription().isEmpty()) {
            throw exception;
        }

        user.get().setPassword(encoder.encode(passwordUpdateData.getPassword()));
        userRepository.save(user.get());
    }
}
