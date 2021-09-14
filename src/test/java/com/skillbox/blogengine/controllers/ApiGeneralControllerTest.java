package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.dto.enums.ModerationStatusRequest;
import com.skillbox.blogengine.model.CaptchaCode;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.orm.CaptchaRepository;
import com.skillbox.blogengine.orm.PostRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiGeneralControllerTest extends AbstractIntegrationTest {
    @Autowired
    CaptchaRepository captchaRepository;
    @Autowired
    private PostRepository postRepository;
    @Value("${blog_engine.additional.uploadedMaxFileWeight}")
    private int FILE_MAX_WEIGHT;

    private static final String LONG_POST_TEXT = "post text 4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...";

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
    void getPostsInitParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));

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
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));

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
                        new Customization("posts[*].timestamp", (o1, o2) -> true),
                        new Customization("posts[*].viewCount", (o1, o2) -> true)));
    }

    @Test
    void getPostsSortedBestTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));

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
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

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
                        new Customization("posts[*].timestamp", (o1, o2) -> true),
                        new Customization("posts[*].viewCount", (o1, o2) -> true)));
    }

    @Test
    void getPostsDefaultParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));

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
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));

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
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

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
                        new Customization("posts[*].timestamp", (o1, o2) -> true),
                        new Customization("posts[*].viewCount", (o1, o2) -> true)));
    }

    @Test
    void getTagsStartWith() throws Exception {
        TagResponse tagResponse = new TagResponse();
        List<TagResponse.WeightedTag> tags = new ArrayList<>();
        tags.add(new TagResponse.WeightedTag("tag1", 1.0));
        tags.add(new TagResponse.WeightedTag("tag2", 0.75));
        tagResponse.setTags(tags);

        String expectedResponse = mapper.writeValueAsString(tagResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "tag")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getTagsMiddleWith() throws Exception {
        TagResponse tagResponse = new TagResponse();
        List<TagResponse.WeightedTag> tags = new ArrayList<>();
        tags.add(new TagResponse.WeightedTag("tag1", 1.0));
        tags.add(new TagResponse.WeightedTag("tag2", 0.75));
        tagResponse.setTags(tags);

        String expectedResponse = mapper.writeValueAsString(tagResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "ag")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getTagsEndWith() throws Exception {
        TagResponse tagResponse = new TagResponse();
        List<TagResponse.WeightedTag> tags = new ArrayList<>();
        tags.add(new TagResponse.WeightedTag("tag2", 1.0));
        tagResponse.setTags(tags);

        String expectedResponse = mapper.writeValueAsString(tagResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "2")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getTagsWithUpperCase() throws Exception {
        TagResponse tagResponse = new TagResponse();
        List<TagResponse.WeightedTag> tags = new ArrayList<>();
        tags.add(new TagResponse.WeightedTag("tag1", 1.0));
        tags.add(new TagResponse.WeightedTag("tag2", 0.75));
        tagResponse.setTags(tags);

        String expectedResponse = mapper.writeValueAsString(tagResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "tAg")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getPostsSearchWithoutParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/search")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true),
                        new Customization("posts[*].viewCount", (o1, o2) -> true)));
    }

    @Test
    void getPostsSearchWithEmptyQueryTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(4, 1588280400,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 4", LONG_POST_TEXT, 0, 0, 0, 1000));
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(3);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("query", "")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsSearchWithCustomQueryTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("query", "3")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getCalendarFor2020YearTest() throws Exception {
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Integer> years = Arrays.asList(2021, 2020);
        Map<String, Long> posts = new HashMap<>();
        posts.put("2020-05-01", 1L);
        calendarResponse.setYears(years);
        calendarResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(calendarResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("year", "2020")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getPostsByDateWithEmptyParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(0);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byDate")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getPostsByDateFor20210201Test() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(2, 1612170000,
                new PostResponse.PostInfo.UserInfo(1, "user_name1"), "title 2", "post text 2...", 0, 0, 1, 10));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byDate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("date", "2021-02-01")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    @Ignore("Не понятно, что должен вернуть метод, если не передали тэг: пустой список постов или посты для всех тэгов")
    void getPostsByTagWithEmptyParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byTag")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsByTagForTag2Test() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        posts.add(new PostResponse.PostInfo(3, 1609491600,
                new PostResponse.PostInfo.UserInfo(2, "user_name2"), "title 3", "post text 3...", 2, 1, 0, 100));

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(1);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byTag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("tag", "tag2")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostsByWrongIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/111")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Document not found", result.getResolvedException().getMessage()));
    }

    @Test
    void getPostsByIdTest() throws Exception {
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        postByIdResponse.setId(2);
        postByIdResponse.setTimestamp(1612180800);
        postByIdResponse.setActive(true);
        postByIdResponse.setUser(new PostByIdResponse.PostUser(1, "user_name1"));
        postByIdResponse.setTitle("title 2");
        postByIdResponse.setText("post text 2");
        postByIdResponse.setLikeCount(0);
        postByIdResponse.setDislikeCount(0);
        postByIdResponse.setViewCount(10);
        PostByIdResponse.Comment comment = new PostByIdResponse.Comment(3,
                1628637087,
                "comment text 3",
                new PostByIdResponse.CommentUser(2, "user_name2", "some link1"));
        postByIdResponse.setComments(Arrays.asList(comment));
        postByIdResponse.setTags(Arrays.asList("tag1"));
        String expectedResponse = mapper.writeValueAsString(postByIdResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/2")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // поскольку нет авторизации, количество просмотров не увеличивается
        String expectedResponse2 = mapper.writeValueAsString(postByIdResponse);
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/2")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("comments[*].timestamp", (o1, o2) -> true)));

        JSONAssert.assertEquals(expectedResponse2, mvcResult2.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("comments[*].timestamp", (o1, o2) -> true)));
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
    void imageUploadTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        MockMultipartFile file = new MockMultipartFile("image.png",
                "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "some xml".getBytes());
        ImageData image = new ImageData();
        image.setImage(file);

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/image")
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .flashAttr("image", image)
                )
                .andExpect(status().isOk());
    }

    @Test
    void uploadWrongNamedImageShouldReturnErrorTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        MockMultipartFile file = new MockMultipartFile("image.png",
                "image.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "some xml".getBytes());
        ImageData image = new ImageData();
        image.setImage(file);

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

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
        when(principal.getName()).thenReturn("test1@mail.ru");

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

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

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
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        PostStatusModerationData moderationData = new PostStatusModerationData();
        moderationData.setPostId(1);
        moderationData.setDecision(ModerationStatusRequest.decline);
        String moderationDataJson = mapper.writeValueAsString(moderationData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

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
        when(principal.getName()).thenReturn("test2@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test2@mail.ru");
        loginData.setPassword("qweqwe2");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/moderation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }
    /**
     * TODO добавить тесты на проверку:
     *
     * 2 из строки announce удаляются html тэги
     */
}
