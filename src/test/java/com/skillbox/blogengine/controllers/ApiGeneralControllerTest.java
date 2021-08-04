package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.dto.InitResponse;
import com.skillbox.blogengine.dto.NotAuthorizedUser;
import com.skillbox.blogengine.dto.PostResponse;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.skillbox.blogengine.initializer.Mysql.mysqlContainer;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiGeneralControllerTest extends AbstractIntegrationTest {

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/init")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getSettingsTest() throws Exception {
        // Поскольку значения полей могут меняться, проверим только их наличие
        mockMvc.perform(MockMvcRequestBuilders.get("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['MULTIUSER_MODE']").exists())
                .andExpect(jsonPath("$['POST_PREMODERATION']").exists())
                .andExpect(jsonPath("$['STATISTICS_IS_PUBLIC']").exists());
    }

    @Test
    void getAuthCheckTest() throws Exception {
        // TODO когда добавится авторизация, добавить логику с авторизованным пользователем
        NotAuthorizedUser notAuthorizedUser = new NotAuthorizedUser();
        String expectedValue = mapper.writeValueAsString(notAuthorizedUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));
    }

    @Test
    void getPostsInitParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));
        posts.add(new PostResponse.PostInfo(3, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "recent")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)
                ));
    }

    @Test
    void getPostsSortedPopularTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(3, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "popular")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsSortedBestTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(3, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "best")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsSortedEarlyTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(3, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "early")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsDefaultParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));
        posts.add(new PostResponse.PostInfo(3, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "")
                .param("limit", "")
                .param("mode", "")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsCustomOffsetTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "2")
                .param("limit", "1")
                .param("mode", "recent")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsCustomLimitTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(4, 1,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", "post text 4...", 0, 0, 0, 1000));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "1")
                .param("mode", "recent")
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }
    /**
     * TODO добавить тесты на проверку:
     * 1 строка announce обрезается до 150 символов
     * 2 из строки announce удаляются html тэги
     */
}
