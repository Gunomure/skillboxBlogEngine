package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.dto.enums.ModerationStatusRequest;
import com.skillbox.blogengine.model.GlobalSetting;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.orm.CaptchaRepository;
import com.skillbox.blogengine.orm.GlobalSettingsRepository;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.UserRepository;
import com.skillbox.blogengine.service.EmailSender;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiGeneralControllerTest extends AbstractIntegrationTest {
    @Autowired
    CaptchaRepository captchaRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    EmailSender emailSender;
    @Value("${blog_engine.additional.uploadedMaxFileWeight}")
    private int FILE_MAX_WEIGHT;

    @Test
    void getInitTest() throws Exception {
        InitResponse initResponse = new InitResponse();
        initResponse.setTitle("DevPub");
        initResponse.setSubtitle("Рассказы разработчиков");
        initResponse.setPhone("+7 903 666-44-55");
        initResponse.setEmail("mail@mail.ru");
        initResponse.setCopyright("Дмитрий Сергеев");
        initResponse.setCopyrightFrom("2005");
        String expectedResponse = mapper.writeValueAsString(initResponse);
        mockMvc.perform(get("/api/init")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getSettingsTest() throws Exception {
        // Поскольку значения полей могут меняться, проверим только их наличие
        mockMvc.perform(get("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['MULTIUSER_MODE']").exists())
                .andExpect(jsonPath("$['POST_PREMODERATION']").exists())
                .andExpect(jsonPath("$['STATISTICS_IS_PUBLIC']").exists());
    }

//    @Test
//    void imageUploadTest() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("image.png",
//                "image.png",
//                MediaType.MULTIPART_FORM_DATA_VALUE,
//                "some xml".getBytes());
//        ImageData image = new ImageData();
//        image.setImage(file);
//
//        loginAsModerator();
//
//        mockMvc.perform(post("/api/image")
//                        .principal(principal)
//                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
//                        .flashAttr("image", image)
//                )
//                .andExpect(status().isOk());
//    }

    @Test
    void uploadWrongNamedImageShouldReturnErrorTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image.png",
                "image.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "some xml".getBytes());
        ImageData image = new ImageData();
        image.setImage(file);

        loginAsModerator();

        mockMvc.perform(post("/api/image")
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .flashAttr("image", image)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Wrong image extension", result.getResolvedException().getMessage()));
    }

    @Test
    void uploadWrongWeightedImageShouldReturnErrorTest() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        final String hugeString = "a";
        for (int i = 0; i < FILE_MAX_WEIGHT + 1; i++) {
            stringBuilder.append(hugeString);
        }

        MockMultipartFile file = new MockMultipartFile("image.png",
                "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                stringBuilder.toString().getBytes());
        ImageData image = new ImageData();
        image.setImage(file);

        loginAsModerator();

        mockMvc.perform(post("/api/image")
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .flashAttr("image", image)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Wrong image weight", result.getResolvedException().getMessage()));
    }

    @Test
    void moderationChangePostStatusTest() throws Exception {
        loginAsModerator();

        PostStatusModerationData moderationData = new PostStatusModerationData();
        moderationData.setPostId(1);
        moderationData.setDecision(ModerationStatusRequest.decline);
        String moderationDataJson = mapper.writeValueAsString(moderationData);

        mockMvc.perform(post("/api/moderation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moderationDataJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result']", is(true)));

        Post postById = postRepository.findPostById(1);
        assertEquals(ModerationStatus.DECLINED, postById.getModerationStatus());
    }

    @Test
    void moderationByWrongUserShouldReturnErrorTest() throws Exception {
        loginAsUser();

        mockMvc.perform(post("/api/moderation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void postMyProfileChangeNameAndEmailTest() throws Exception {
        loginAsModerator();

        ProfileData profileData = new ProfileData();
        profileData.setName("moder");
        profileData.setEmail("moder@mail.ru");

        String requestData = mapper.writeValueAsString(profileData);
        String expectedValue = mapper.writeValueAsString(new SimpleResponse(true));

        mockMvc.perform(post("/api/profile/my")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        Optional<User> persistedUser = userRepository.findByEmail("moder@mail.ru");
        assertTrue(persistedUser.isPresent());
        assertEquals("moder", persistedUser.get().getName());
    }

    @Test
    @Transactional
    void postMyProfileRegisteredUserTest() throws Exception {
        loginAsUser();

        ProfileData profileData = new ProfileData();
        profileData.setName("user_name2");
        profileData.setEmail("test3@mail.ru");

        String requestData = mapper.writeValueAsString(profileData);

        mockMvc.perform(post("/api/profile/my")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("User already exists", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", Matchers.is(false)))
                .andExpect(jsonPath("$.errors.email", Matchers.is("Этот e-mail уже зарегистрирован")));
    }

    @Test
    @Transactional
    void postMyProfileChangePasswordTest() throws Exception {
        loginAsUser();

        String userPasswordBefore = userRepository.findByEmail("test2@mail.ru").get().getPassword();

        ProfileData profileData = new ProfileData();
        profileData.setPassword("123456");

        String requestData = mapper.writeValueAsString(profileData);
        String expectedValue = mapper.writeValueAsString(new SimpleResponse(true));

        mockMvc.perform(post("/api/profile/my")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        String userPasswordAfter = userRepository.findByEmail("test2@mail.ru").get().getPassword();
        assertNotEquals(userPasswordBefore, userPasswordAfter);
    }

    @Test
    @Transactional
    void postMyProfileTooShortPasswordTest() throws Exception {
        loginAsUser();

        ProfileData profileData = new ProfileData();
        profileData.setPassword("");

        String requestData = mapper.writeValueAsString(profileData);

        mockMvc.perform(post("/api/profile/my")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Password is too short", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", Matchers.is(false)))
                .andExpect(jsonPath("$.errors.password", Matchers.is("Пароль короче 6-ти символов")));
    }

    @Test
    void getMyStatisticsTest() throws Exception {
        loginAsUser();
        BlogStatisticsResponse response = new BlogStatisticsResponse(
                4, 2, 1, 1300, 1588291200
        );
        String expectedValue = mapper.writeValueAsString(response);

        mockMvc.perform(get("/api/statistics/my")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    void getAllStatisticsTest() throws Exception {
        loginAsUser();
        BlogStatisticsResponse response = new BlogStatisticsResponse(
                14, 2, 1, 115313, 1588291200
        );
        String expectedValue = mapper.writeValueAsString(response);

        mockMvc.perform(get("/api/statistics/all")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    @Transactional
    void putGlobalSettingsTest() throws Exception {
        loginAsModerator();

        GlobalSettingsData setting = new GlobalSettingsData();
        setting.setMultiuserMode(true);
        setting.setPostPremoderation(true);
        setting.setStatisticsIsPublic(true);
        String requestData = mapper.writeValueAsString(setting);

        mockMvc.perform(put("/api/settings")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
                )
                .andDo(print())
                .andExpect(status().isOk());

        List<GlobalSetting> allSettings = globalSettingsRepository.findAll();
        for (GlobalSetting item : allSettings) {
            assertEquals("YES", item.getValue());
        }
    }
}
