package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.custom.CommentUserInfo;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.model.custom.PostWithComments;
import com.skillbox.blogengine.model.custom.PostsCountPerDate;
import com.skillbox.blogengine.orm.PostCommentsRepository;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final TagRepository tagRepository;

    private static final int ANNOUNCE_MAX_LENGTH = 150;

    public PostService(PostRepository postRepository, PostCommentsRepository postCommentsRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagRepository = tagRepository;
    }

    public PostResponse selectAllPostsByParameters(int offset, int limit, ModeType mode) {
        PageRequest pageRequest;
        switch (mode) {
            case popular:
                pageRequest = PageRequest.of(offset, limit, Sort.by("commentCount").descending());
                break;
            case best:
                pageRequest = PageRequest.of(offset, limit, Sort.by("likeCount").descending());
                break;
            case early:
                pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
                break;
            default:
//                 recent тоже считается дефолтным
                pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").descending());
                break;
        }

        return mapToPostResponse(postRepository.findPostsInfoPageable("", pageRequest));
    }

    public PostResponse selectFilteredPostsByParameters(int offset, int limit, String query) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
        return mapToPostResponse(postRepository.findPostsInfoPageable(query, pageRequest));
    }

    public PostResponse selectPostsByDate(int offset, int limit, String date) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
        return mapToPostResponse(postRepository.findPostsInfoByDate(date, pageRequest));
    }

    public PostResponse selectPostsByTag(int offset, int limit, String tag) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
        return mapToPostResponse(postRepository.findPostsInfoByTag(tag, pageRequest));
    }

    public PostByIdResponse selectPostsById(int postId) {
        // заполняем comments в response
        PostWithComments postInfoById = postRepository.findPostInfoById(postId);
        List<CommentUserInfo> commentsByPostId;
        List<String> tagsByPostId;
        if (postInfoById != null) {
            commentsByPostId = postCommentsRepository.findCommentsByPostId(postId);
            // заполняем основную часть response
            tagsByPostId = tagRepository.findTagsByPostId(postId);
            Post postById = postRepository.findPostById(postId);
            postById.setViewCount(postById.getViewCount() + 1);
            postRepository.save(postById);
        } else {
            throw new EntityNotFoundException("Document not found");
        }
        return mapToPostByIdResponse(postInfoById, commentsByPostId, tagsByPostId);
    }

    public PostResponse selectMyPosts(int userId, int offset, int limit, PostStatus status) {
        PostResponse response = new PostResponse();
        PageRequest pageRequest = PageRequest.of(offset, limit);
        switch (status) {
            case inactive:
                response = mapToPostResponse(postRepository.findInactivePosts(userId, pageRequest));
                break;
            case pending:
                response = mapToPostResponse(postRepository.findPendingPosts(userId, pageRequest));
                break;
            case declined:
                response = mapToPostResponse(postRepository.findDeclinedPosts(userId, pageRequest));
                break;
            case published:
                response = mapToPostResponse(postRepository.findPublishedPosts(userId, pageRequest));
                break;
        }

        return response;
    }

    public long count() {
        return postRepository.count();
    }

    public CalendarResponse selectPostsCountsByYear(int year) {
        List<PostsCountPerDate> postsCountPerDates = postRepository.findPostsCountPerDateByYear(year);
        List<Integer> distinctByTime = postRepository.findDistinctYears();
        return mapToPostsCountsPerYear(distinctByTime, postsCountPerDates);
    }

    private CalendarResponse mapToPostsCountsPerYear(List<Integer> years, List<PostsCountPerDate> postsCountPerDates) {
        Map<String, Long> posts = new HashMap<>();
        for (PostsCountPerDate item : postsCountPerDates) {
            posts.put(item.getPostsDate(), item.getPostsCount());
        }
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(years);
        calendarResponse.setPosts(posts);
        return calendarResponse;
    }

    private PostByIdResponse mapToPostByIdResponse(PostWithComments postWithComments,
                                                   List<CommentUserInfo> commentsByPostId,
                                                   List<String> tags) {
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        postByIdResponse.setId(postWithComments.getId());
        postByIdResponse.setTimestamp(postWithComments.getTimestamp());
        postByIdResponse.setActive(postWithComments.isActive());

        postByIdResponse.setUser(new PostByIdResponse.PostUser(postWithComments.getUserId(),
                postWithComments.getUserName()));
        postByIdResponse.setTitle(postWithComments.getTitle());
        postByIdResponse.setText(postWithComments.getText());
        postByIdResponse.setLikeCount(postWithComments.getLikeCount());
        postByIdResponse.setDislikeCount(postWithComments.getDislikeCount());
        postByIdResponse.setViewCount(postWithComments.getViewCount());

        List<PostByIdResponse.Comment> comments = new ArrayList<>();
        for (CommentUserInfo commentByPostId : commentsByPostId) {
            PostByIdResponse.Comment comment = new PostByIdResponse.Comment(commentByPostId.getId(),
                    commentByPostId.getTimestamp(), commentByPostId.getText(), new PostByIdResponse.CommentUser(
                    commentByPostId.getUserId(), commentByPostId.getUserName(), commentByPostId.getUserPhoto()
            ));
            comments.add(comment);
        }
        postByIdResponse.setComments(comments);

        postByIdResponse.setTags(tags);
        return postByIdResponse;
    }

    public static PostResponse mapToPostResponse(List<PostUserCounts> postsAdditionalInfo) {
        PostResponse response = new PostResponse();
        response.setCount(postsAdditionalInfo.size());

        List<PostResponse.PostInfo> posts = new ArrayList<>();
        for (PostUserCounts item : postsAdditionalInfo) {
            posts.add(createPostInfo(item));
        }
        response.setPosts(posts);
        return response;
    }

    private static PostResponse.PostInfo createPostInfo(PostUserCounts post) {
        // так проще всего удалить html тэги
        String announceWithoutTags = Jsoup.parse(post.getAnnounce()).text();
        if (announceWithoutTags.length() > ANNOUNCE_MAX_LENGTH) {
            announceWithoutTags = announceWithoutTags.substring(0, ANNOUNCE_MAX_LENGTH);
        }
        // TODO не понятно, добавлять многоточие всегда или если длина >150
        announceWithoutTags += "...";

        return new PostResponse.PostInfo(post.getId(), post.getTimestamp(), new PostResponse.PostInfo.UserInfo(post.getUserId(), post.getUserName()),
                post.getTitle(), announceWithoutTags, post.getLikeCount(), post.getDislikeCount(), post.getCommentCount(), post.getViewCount());
    }
}
