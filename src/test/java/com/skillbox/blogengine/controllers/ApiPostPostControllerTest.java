package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.dto.PostAddRequest;
import com.skillbox.blogengine.dto.PostVoteData;
import com.skillbox.blogengine.dto.SimpleResponse;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.PostVote;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.PostVoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class ApiPostPostControllerTest extends AbstractIntegrationTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;

    @Test
    void moderationAdditionPostTest() throws Exception {
        loginAsModerator();
        PostAddRequest postAddRequest = new PostAddRequest();
        postAddRequest.setTimestamp(1612170000);
        postAddRequest.setActive(true);
        postAddRequest.setTitle("new test title");
        postAddRequest.setTags(List.of("tag1"));
        postAddRequest.setText("text to test addition post 1111111111111111111111111111111111111111111111111111111");

        SimpleResponse simpleResponse = new SimpleResponse(true);
        String postData = mapper.writeValueAsString(postAddRequest);
        String expectedValue = mapper.writeValueAsString(simpleResponse);

        mockMvc.perform(post("/api/post")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        Post lastPost = postRepository.findDesc().get(0);
        assertEquals(ModerationStatus.NEW, lastPost.getModerationStatus());
        assertTrue(lastPost.isActive());
        assertEquals("new test title", lastPost.getTitle());
        assertEquals("text to test addition post 1111111111111111111111111111111111111111111111111111111", lastPost.getText());
        assertFalse(lastPost.getTagToPost().isEmpty());
    }

    @Test
    void moderationAdditionPostWrongDataTest() throws Exception {
        loginAsModerator();
        PostAddRequest postAddRequest = new PostAddRequest();
        postAddRequest.setTimestamp(1612170000);
        postAddRequest.setActive(true);
        postAddRequest.setTitle("ne");
        postAddRequest.setTags(List.of("tag1"));
        postAddRequest.setText("text");

        String postData = mapper.writeValueAsString(postAddRequest);

        mockMvc.perform(post("/api/post")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.errors.title", is("Заголовок не установлен")))
                .andExpect(jsonPath("$.errors.text", is("Текст публикации слишком короткий")));
    }

    @Test
    void changePostByModeratorTest() throws Exception {
        loginAsModerator();

        Post postBefore = postRepository.findPostById(3);
        PostAddRequest postAddRequest = new PostAddRequest();
        postAddRequest.setTimestamp(1612170000);
        postAddRequest.setActive(true);
        postAddRequest.setTitle("new test title");
        postAddRequest.setTags(List.of("tag1"));
        postAddRequest.setText("text to test addition post 1111111111111111111111111111111111111111111111111111111");

        SimpleResponse simpleResponse = new SimpleResponse(true);
        String postData = mapper.writeValueAsString(postAddRequest);
        String expectedValue = mapper.writeValueAsString(simpleResponse);

        mockMvc.perform(put("/api/post/3")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        Post postAfter = postRepository.findPostById(3);
        assertEquals(postBefore.getModerationStatus(), postAfter.getModerationStatus()); //статус должен не изменяться
        assertTrue(postAfter.isActive());
        assertEquals("new test title", postAfter.getTitle());
        assertEquals("text to test addition post 1111111111111111111111111111111111111111111111111111111", postAfter.getText());
        assertFalse(postAfter.getTagToPost().isEmpty());
    }

    @Test
    void changePostByAuthorTest() throws Exception {
        loginAsUser();
        PostAddRequest postAddRequest = new PostAddRequest();
        postAddRequest.setTimestamp(1612170000);
        postAddRequest.setActive(true);
        postAddRequest.setTitle("new test title");
        postAddRequest.setTags(List.of("tag1"));
        postAddRequest.setText("text to test addition post 1111111111111111111111111111111111111111111111111111111");

        SimpleResponse simpleResponse = new SimpleResponse(true);
        String postData = mapper.writeValueAsString(postAddRequest);
        String expectedValue = mapper.writeValueAsString(simpleResponse);

        mockMvc.perform(put("/api/post/3")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        Post lastPost = postRepository.findPostById(3);
        assertEquals(ModerationStatus.NEW, lastPost.getModerationStatus()); // статус автоматически меняется на NEW
        assertTrue(lastPost.isActive());
        assertEquals("new test title", lastPost.getTitle());
        assertEquals("text to test addition post 1111111111111111111111111111111111111111111111111111111", lastPost.getText());
        assertFalse(lastPost.getTagToPost().isEmpty());
    }

    @Test
    void changePostWrongDataTest() throws Exception {
        loginAsModerator();
        PostAddRequest postAddRequest = new PostAddRequest();
        postAddRequest.setTimestamp(1612170000);
        postAddRequest.setActive(true);
        postAddRequest.setTitle("ne");
        postAddRequest.setTags(List.of("tag1"));
        postAddRequest.setText("text");

        String postData = mapper.writeValueAsString(postAddRequest);

        mockMvc.perform(put("/api/post/3")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.errors.title", is("Заголовок слишком короткий")))
                .andExpect(jsonPath("$.errors.text", is("Текст публикации слишком короткий")));
    }

    @Test
    void postLikeTest() throws Exception {
        loginAsModerator();

        PostVoteData postVoteData = new PostVoteData(2);
        String postData = mapper.writeValueAsString(postVoteData);
        String expectedValue = mapper.writeValueAsString(new SimpleResponse(true));

        mockMvc.perform(post("/api/post/like")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        PostVote voteAfter = postVoteRepository.findByUserAndPostId(1, 2);
        assertEquals(1, voteAfter.getValue());
    }

    @Test
    void postDislikeTest() throws Exception {
        loginAsModerator();

        PostVoteData postVoteData = new PostVoteData(2);
        String postData = mapper.writeValueAsString(postVoteData);
        String expectedValue = mapper.writeValueAsString(new SimpleResponse(true));

        mockMvc.perform(post("/api/post/dislike")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postData)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedValue));

        PostVote voteAfter = postVoteRepository.findByUserAndPostId(1, 2);
        assertEquals(-1, voteAfter.getValue());
    }
}
