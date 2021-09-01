package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.ErrorResponse;
import com.skillbox.blogengine.dto.SimpleResponse;
import com.skillbox.blogengine.dto.UserRegisterData;
import com.skillbox.blogengine.model.CaptchaCode;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.CaptchaRepository;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegisterService {
    private final static Logger LOGGER = LogManager.getLogger(RegisterService.class);

    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;

    public RegisterService(UserRepository userRepository, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
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
            newUser.setPassword(user.getPassword());
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
}
