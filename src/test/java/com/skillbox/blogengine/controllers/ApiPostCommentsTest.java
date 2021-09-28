package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.AdditionCommentResponse;
import com.skillbox.blogengine.dto.CommentData;
import com.skillbox.blogengine.model.PostComment;
import com.skillbox.blogengine.orm.PostCommentsRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ApiPostCommentsTest extends AbstractIntegrationTest {

    @Autowired
    PostCommentsRepository postCommentsRepository;

    @Test
    void addCommentWithoutParentTest() throws Exception {

        CommentData commentData = new CommentData();
        commentData.setParentId(null);
        commentData.setPostId(1);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        loginAsModerator();

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
        loginAsUser();
        CommentData commentData1 = new CommentData();
        commentData1.setPostId(3);
        commentData1.setText("parent comment");
        commentData1.setParentId(null);

        String commentDataString1 = mapper.writeValueAsString(commentData1);

        MvcResult mvcResult = mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataString1)
                )
                .andExpect(status().isOk())
                .andReturn();
        int commentId = new JSONObject(mvcResult.getResponse().getContentAsString()).getInt("id");
        CommentData commentData2 = new CommentData();
        commentData2.setPostId(3);
        commentData2.setText("child comment");
        commentData2.setParentId(commentId);
        String commentDataString2 = mapper.writeValueAsString(commentData2);

        MvcResult mvcResult2 = mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataString2)
                )
                .andExpect(status().isOk())
                .andReturn();
        int commentId2 = new JSONObject(mvcResult2.getResponse().getContentAsString()).getInt("id");

        PostComment parentComment = postCommentsRepository.findById(commentId).get();
        PostComment childComment = postCommentsRepository.findById(commentId2).get();

        assertNull(parentComment.getParent());
        assertEquals(3, parentComment.getPost().getId());
        assertEquals(2, parentComment.getUser().getId());
        assertEquals("parent comment", parentComment.getText());

        assertEquals(parentComment.getId(), childComment.getParent().getId());
        assertEquals(3, childComment.getPost().getId());
        assertEquals(2, childComment.getUser().getId());
        assertEquals("child comment", childComment.getText());
    }

    @Test
    void additionTooShortCommentShouldReturnErrorTest() throws Exception {
        CommentData commentData = new CommentData();
        commentData.setParentId(2);
        commentData.setPostId(1);
        commentData.setText("te");
        String commentDataJson = mapper.writeValueAsString(commentData);

        loginAsModerator();

        mockMvc.perform(post("/api/comment")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Comment text is empty or too short", result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.errors.text", is("Текст комментария не задан или слишком короткий")));
    }

    @Test
    void additionCommentForWrongPostShouldReturnErrorTest() throws Exception {
        CommentData commentData = new CommentData();
        commentData.setParentId(2);
        commentData.setPostId(999);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        loginAsModerator();

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
        CommentData commentData = new CommentData();
        commentData.setParentId(999);
        commentData.setPostId(1);
        commentData.setText("test text");
        String commentDataJson = mapper.writeValueAsString(commentData);

        loginAsModerator();

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
