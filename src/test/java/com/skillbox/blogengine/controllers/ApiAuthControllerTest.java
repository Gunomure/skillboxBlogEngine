package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.model.CaptchaCode;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.CaptchaRepository;
import com.skillbox.blogengine.orm.UserRepository;
import com.skillbox.blogengine.service.EmailSender;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiAuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    EmailSender emailSender;
    @Autowired
    CaptchaRepository captchaRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void testSending() throws Exception {
        RestorePasswordData passwordData = new RestorePasswordData("moder@mail.ru");
        String passwordDataJson = mapper.writeValueAsString(passwordData);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    void postAuthRegisterTest() throws Exception {
        MvcResult mvcResultCaptcha = mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/captcha")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResultCaptcha.getResponse().getContentAsString();
        CaptchaResponse captchaResponse = mapper.readValue(contentAsString, CaptchaResponse.class);
        System.out.println(captchaResponse.getSecret());
        System.out.println(captchaResponse.getImage());
        CaptchaCode captchaCodeBySecretCode = captchaRepository.findCaptchaCodeBySecretCode(captchaResponse.getSecret());
        System.out.println(captchaCodeBySecretCode.getCode());

        UserRegisterData registerData = new UserRegisterData("email@email.ru",
                "qweqwe",
                "dtyunyaev",
                captchaCodeBySecretCode.getCode(),
                captchaResponse.getSecret());

        SimpleResponse simpleResponse = new SimpleResponse(true);

        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Этот e-mail уже зарегистрирован");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setResult(false);
        errorResponse.setErrors(errors);

        String registrationContent = mapper.writeValueAsString(registerData);

        String expectedResponse = mapper.writeValueAsString(simpleResponse);
        String expectedErrorResponse = mapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationContent)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        // при повторной регистрации должно выдать, что такой email уже существует
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationContent)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedErrorResponse));
    }

    @Test
    void getAuthCheckTest() throws Exception {
        // TODO когда добавится авторизация, добавить логику с авторизованным пользователем
        Map<String, Boolean> notAuthorizedUserResponse = new HashMap<>();
        notAuthorizedUserResponse.put("result", false);
        String expectedValue = mapper.writeValueAsString(notAuthorizedUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    void getAuthCheckAuthorisedAsModerTest() throws Exception {
        loginAsModerator();
        LoggedUserResponse loggedUserResponse = new LoggedUserResponse();
        LoggedUserResponse.UserLoggedData user = new LoggedUserResponse.UserLoggedData(
                1, "user_name1", null, "test1@mail.ru", true, 4, true
        );
        loggedUserResponse.setUser(user);
        loggedUserResponse.setResult(true);
        String expectedValue = mapper.writeValueAsString(loggedUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    void getAuthCheckAuthorisedAsUserTest() throws Exception {
        loginAsUser();
        LoggedUserResponse loggedUserResponse = new LoggedUserResponse();
        LoggedUserResponse.UserLoggedData user = new LoggedUserResponse.UserLoggedData(
                2, "user_name2", "some link1", "test2@mail.ru", false, 0, false
        );
        loggedUserResponse.setUser(user);
        loggedUserResponse.setResult(true);
        String expectedValue = mapper.writeValueAsString(loggedUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    void getCaptchaTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/captcha")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['secret']").isNotEmpty())
                .andExpect(jsonPath("$['image']").isNotEmpty());
    }

    @Test
    @Transactional
    void postRestorePasswordTest() throws Exception {
        User user = userRepository.findByEmail("test3@mail.ru").get();
        CaptchaCode captcha = captchaRepository.findCaptchaCodeBySecretCode("eqKIqurpZs");
        String newPassword = "123456";
        PasswordUpdateData passwordUpdateData = new PasswordUpdateData(
                user.getCode(), newPassword, captcha.getCode(), captcha.getSecretCode()
        );

        String passwordDataJson = mapper.writeValueAsString(passwordUpdateData);
        String passwordBefore = user.getPassword();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isOk());
        String passwordAfter = userRepository.findByEmail("test3@mail.ru").get().getPassword();
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    @Transactional
    void postRestorePasswordTooShortPasswordTest() throws Exception {
        User user = userRepository.findByEmail("test3@mail.ru").get();
        CaptchaCode captcha = captchaRepository.findCaptchaCodeBySecretCode("eqKIqurpZs");
        String newPassword = "";
        PasswordUpdateData passwordUpdateData = new PasswordUpdateData(
                user.getCode(), newPassword, captcha.getCode(), captcha.getSecretCode()
        );
        String passwordDataJson = mapper.writeValueAsString(passwordUpdateData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Error while checking data for restore password", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", Matchers.is(false)))
                .andExpect(jsonPath("$.errors.password", Matchers.is("Пароль короче 6-ти символов")));
    }

    @Test
    @Transactional
    void postRestorePasswordWrongCodeTest() throws Exception {
        CaptchaCode captcha = captchaRepository.findCaptchaCodeBySecretCode("eqKIqurpZs");
        String newPassword = "123456";
        PasswordUpdateData passwordUpdateData = new PasswordUpdateData(
                "randomCode", newPassword, captcha.getCode(), captcha.getSecretCode()
        );
        String passwordDataJson = mapper.writeValueAsString(passwordUpdateData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Error while checking data for restore password", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", Matchers.is(false)))
                .andExpect(jsonPath("$.errors.code", Matchers.is("Ссылка для восстановления пароля устарела.<a href= \"/login/restore-password\">Запросить ссылку снова</a>")));
    }

    @Test
    @Transactional
    void postRestorePasswordWrongCaptchaTest() throws Exception {
        User user = userRepository.findByEmail("test3@mail.ru").get();
        CaptchaCode captcha = captchaRepository.findCaptchaCodeBySecretCode("eqKIqurpZs");
        String newPassword = "123456";
        PasswordUpdateData passwordUpdateData = new PasswordUpdateData(
                user.getCode(), newPassword, "randomCaptchaCode", captcha.getSecretCode()
        );
        String passwordDataJson = mapper.writeValueAsString(passwordUpdateData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Error while checking data for restore password", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", Matchers.is(false)))
                .andExpect(jsonPath("$.errors.captcha", Matchers.is("Код с картинки введён неверно")));
    }
}
