package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.controller.exception.UserNotAuthorizedException;
import com.skillbox.blogengine.dto.AdditionCommentResponse;
import com.skillbox.blogengine.dto.CommentData;
import com.skillbox.blogengine.model.PostComment;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.PostCommentsRepository;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostCommentsService {
    private final static Logger LOGGER = LogManager.getLogger(PostService.class);

    private final PostCommentsRepository postCommentsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${blog_engine.additional.commentMinLength}")
    private int COMMENT_MIN_LENGTH;

    public PostCommentsService(PostCommentsRepository postCommentsRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postCommentsRepository = postCommentsRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public AdditionCommentResponse addComment(CommentData commentData, String userEmail) {
        if (commentData.getText().isEmpty() || commentData.getText().length() < COMMENT_MIN_LENGTH) {
            BadRequestException exception = new BadRequestException("Comment text is empty or too short");
            exception.addErrorDescription("text", "Текст комментария не задан или слишком короткий");
            throw exception;
        }
        if (!postRepository.existsById(commentData.getPostId())) {
            BadRequestException exception = new BadRequestException(String.format("Post %d does not exist",
                    commentData.getPostId()));
            exception.addErrorDescription("post_id", String.format("Пост %d не существует",
                    commentData.getPostId()));
            throw exception;
        }
        if (commentData.getParentId() != null && !postCommentsRepository.existsById(commentData.getParentId())) {
            BadRequestException exception = new BadRequestException(String.format("Parent comment %d does not exist",
                    commentData.getParentId()));
            exception.addErrorDescription("parent_id", String.format("Родительский комментарий %d не существует",
                    commentData.getParentId()));
            throw exception;
        }

        Optional<User> user = userRepository.findByEmail(userEmail);
        PostComment postComment = new PostComment();
        postComment.setPost(postRepository.findPostById(commentData.getPostId()));
        if (commentData.getParentId() != null) {
            postComment.setParent(postCommentsRepository.findById(commentData.getParentId()).orElse(null));
        }
        postComment.setUser(user.orElseThrow(() -> new UserNotAuthorizedException(
                String.format("User %s not found", userEmail))));
        postComment.setText(commentData.getText());
        postComment.setTime(LocalDateTime.now());
        PostComment savedComment = postCommentsRepository.save(postComment);

        return new AdditionCommentResponse(savedComment.getId());
    }

}
