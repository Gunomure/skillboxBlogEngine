package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.CalendarResponse;
import com.skillbox.blogengine.dto.PostByIdResponse;
import com.skillbox.blogengine.dto.PostResponse;
import com.skillbox.blogengine.dto.TagResponse;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ApiGetPostControllerTest extends AbstractIntegrationTest {
    private static final String LONG_POST_TEXT = "post text 4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...";

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
    void getPostsByTagWithEmptyParametersTest() throws Exception {
        List<PostResponse.PostInfo> posts = new ArrayList<>();

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(posts);
        postResponse.setCount(0);
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
    void getPostModerationSortedNewTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                1, 1632341551, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 1", "post text 1...", 0, 0, 2, 1
        ));
        posts.add(new PostResponse.PostInfo(
                5, 1632341551, new PostResponse.PostInfo.UserInfo(3, "user_name3"),
                "title 5", "post text 5...", 0, 0, 0, 1000
        ));
        posts.add(new PostResponse.PostInfo(
                6, 1632341551, new PostResponse.PostInfo.UserInfo(3, "user_name3"),
                "title 6", "post text 6...", 0, 0, 0, 1000
        ));
        posts.add(new PostResponse.PostInfo(
                12, 1632341551, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 12", "post text 12...", 0, 0, 0, 0
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "new")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostModerationSortedDeclinedTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                7, 1632415765, new PostResponse.PostInfo.UserInfo(3, "user_name3"),
                "title 7", "post text 7...", 0, 0, 0, 1000
        ));
        posts.add(new PostResponse.PostInfo(
                8, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 8", "post text 8...", 0, 0, 0, 10000
        ));
        posts.add(new PostResponse.PostInfo(
                9, 1632341551, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 9", "post text 9...", 0, 0, 0, 100000
        ));
        posts.add(new PostResponse.PostInfo(
                13, 1632341551, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 13", "post text 13...", 0, 0, 0, 2
        ));
        posts.add(new PostResponse.PostInfo(
                14, 1632341551, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 14", "post text 14...", 0, 0, 0, 2
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "declined")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostModerationSortedAcceptedTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                4, 1632415765, new PostResponse.PostInfo.UserInfo(2, "user_name2"),
                "title 4", LONG_POST_TEXT, 0, 0, 0, 1000
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "accepted")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostMyInactiveTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                10, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 10", "post text 10...", 0, 0, 0, 999
        ));
        posts.add(new PostResponse.PostInfo(
                11, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 11", "post text 11...", 0, 0, 0, -1
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "inactive")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostMyPendingTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                1, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 1", "post text 1...", 0, 0, 2, 1
        ));
        posts.add(new PostResponse.PostInfo(
                12, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 12", "post text 12...", 0, 0, 0, 0
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "pending")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostMyDeclinedTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                8, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 8", "post text 8...", 0, 0, 0, 10000
        ));
        posts.add(new PostResponse.PostInfo(
                9, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 9", "post text 9...", 0, 0, 0, 100000
        ));
        posts.add(new PostResponse.PostInfo(
                13, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 13", "post text 13...", 0, 0, 0, 2
        ));
        posts.add(new PostResponse.PostInfo(
                14, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 14", "post text 14...", 0, 0, 0, 2
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "declined")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }

    @Test
    void getPostMyPublishedTest() throws Exception {
        loginAsModerator();
        List<PostResponse.PostInfo> posts = new ArrayList<>();
        PostResponse postResponse = new PostResponse();
        posts.add(new PostResponse.PostInfo(
                2, 1632415765, new PostResponse.PostInfo.UserInfo(1, "user_name1"),
                "title 2", "post text 2...", 0, 0, 1, 10
        ));
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        String expectedResponse = mapper.writeValueAsString(postResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("status", "published")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // игнорируем поле timestamp, так как оно может меняться в зависимости от времени запуска миграции
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(),
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("posts[*].timestamp", (o1, o2) -> true)));
    }
}
