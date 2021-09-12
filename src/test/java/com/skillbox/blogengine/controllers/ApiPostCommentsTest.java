package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.AdditionCommentResponse;
import com.skillbox.blogengine.dto.CommentData;
import com.skillbox.blogengine.dto.LoginData;
import com.skillbox.blogengine.model.PostComment;
import com.skillbox.blogengine.orm.PostCommentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiPostCommentsTest extends AbstractIntegrationTest {

    @Autowired
    PostCommentsRepository postCommentsRepository;

    @Test
    void addCommentWithoutParentTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        CommentData commentData = new CommentData();
        commentData.setParentId(null);
        commentData.setPostId(1);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        AdditionCommentResponse additionCommentResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), AdditionCommentResponse.class);
        Optional<PostComment> commentFromDb = postCommentsRepository.findById(additionCommentResponse.getId());

        assertTrue(commentFromDb.isPresent());
        assertEquals(1, commentFromDb.get().getPost().getId());
        assertNull(commentFromDb.get().getParent());
        assertEquals("test text", commentFromDb.get().getText());
    }

    @Test
    void addCommentWithParentTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        CommentData commentData = new CommentData();
        commentData.setParentId(2);
        commentData.setPostId(1);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        AdditionCommentResponse additionCommentResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), AdditionCommentResponse.class);
        Optional<PostComment> commentFromDb = postCommentsRepository.findById(additionCommentResponse.getId());

        assertTrue(commentFromDb.isPresent());
        assertEquals(1, commentFromDb.get().getPost().getId());
        assertEquals(2, commentFromDb.get().getParent().getId());
        assertEquals("test text", commentFromDb.get().getText());
    }

    @Test
    void additionTooShortCommentShouldReturnErrorTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        CommentData commentData = new CommentData();
        commentData.setParentId(2);
        commentData.setPostId(1);
        commentData.setText("te");
        String commentDataJson = mapper.writeValueAsString(commentData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Comment text is empty or too short", result.getResolvedException().getMessage()));
    }

    @Test
    void additionCommentForWrongPostShouldReturnErrorTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        CommentData commentData = new CommentData();
        commentData.setParentId(2);
        commentData.setPostId(999);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Post 999 does not exist", result.getResolvedException().getMessage()));
    }

    @Test
    void additionCommentForWrongParentShouldReturnErrorTest() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        CommentData commentData = new CommentData();
        commentData.setParentId(999);
        commentData.setPostId(1);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Parent comment 999 does not exist", result.getResolvedException().getMessage()));
    }
}
