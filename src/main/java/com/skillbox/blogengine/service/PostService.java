package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.controller.exception.SimpleException;
import com.skillbox.blogengine.controller.exception.UserNotAuthorizedException;
import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.dto.enums.ModeType;
import com.skillbox.blogengine.dto.enums.ModerationStatusRequest;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.PostVote;
import com.skillbox.blogengine.model.Tag;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.model.custom.CommentUserInfo;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.model.custom.PostWithComments;
import com.skillbox.blogengine.model.custom.PostsCountPerDate;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.orm.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class PostService {

    private final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Value("${blog_engine.additional.announceMaxLength}")
    private int ANNOUNCE_MAX_LENGTH;
    @Value("${blog_engine.additional.postTitleMinLength}")
    private int POST_TITLE_MIN_LENGTH;
    @Value("${blog_engine.additional.postTextMinLength}")
    private int POST_TEXT_MIN_LENGTH;

    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentsRepository postCommentsRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
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

    public PostByIdResponse selectPostsById(int postId, Principal principal) {
        Optional<User> currentUser = userRepository.findByEmail(principal != null ? principal.getName() : "");

        // заполняем comments в response
        PostWithComments postInfoById = postRepository.findPostInfoById(postId);
        List<CommentUserInfo> commentsByPostId;
        List<String> tagsByPostId;
        if (postInfoById != null) {
            commentsByPostId = postCommentsRepository.findCommentsByPostId(postId);
            // заполняем основную часть response
            tagsByPostId = tagRepository.findTagsByPostId(postId);
            Post postById = postRepository.findPostById(postId);
            // увеличиваем просмотры, если пользователь не модератор и не автор поста
            if (currentUser.isPresent() && !currentUser.get().isModerator()
                    && currentUser.get().getId() != postInfoById.getUserId()) {
                postById.setViewCount(postById.getViewCount() + 1);
            }

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
            case INACTIVE:
                response = mapToPostResponse(postRepository.findInactivePosts(userId, pageRequest));
                break;
            case PENDING:
                response = mapToPostResponse(postRepository.findPendingPosts(userId, pageRequest));
                break;
            case DECLINED:
                response = mapToPostResponse(postRepository.findDeclinedPosts(userId, pageRequest));
                break;
            case PUBLISHED:
                response = mapToPostResponse(postRepository.findPublishedPosts(userId, pageRequest));
                break;
        }

        return response;
    }

    public PostResponse selectModerationPosts(int userId, int offset, int limit, ModerationStatus status) {
        PostResponse response = new PostResponse();
        PageRequest pageRequest = PageRequest.of(offset, limit);
        switch (status) {
            case NEW:
                response = mapToPostResponse(postRepository.findNewPosts(userId, pageRequest));
                break;
            case ACCEPTED:
                response = mapToPostResponse(postRepository.findModeratorAccepted(userId, pageRequest));
                break;
            case DECLINED:
                response = mapToPostResponse(postRepository.findModeratorDeclinedPosts(userId, pageRequest));
                break;
        }

        return response;
    }

    public SimpleResponse updatePost(int postId, PostAddRequest requestData, String userEmail) {
        if (requestData.getTitle().length() < POST_TITLE_MIN_LENGTH ||
                requestData.getText().length() < POST_TEXT_MIN_LENGTH) {
            ErrorResponse errorResponse = new ErrorResponse();
            if (requestData.getTitle().length() < POST_TITLE_MIN_LENGTH) {
                errorResponse.addError("title", "Заголовок слишком короткий");
            }
            if (requestData.getText().length() < POST_TEXT_MIN_LENGTH) {
                errorResponse.addError("text", "Текст публикации слишком короткий");
            }
            return errorResponse;
        } else {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User " + userEmail + " not found"));
            Post post = postRepository.findPostById(postId);

            fillPost(post, requestData);
            if (!user.isModerator()) {
                post.setModerationStatus(ModerationStatus.NEW);
            }

            postRepository.save(post);
            return new SimpleResponse(true);
        }
    }

    public SimpleResponse addPost(PostAddRequest requestData, String userEmail) {
        if (requestData.getTitle().length() < POST_TITLE_MIN_LENGTH ||
                requestData.getText().length() < POST_TEXT_MIN_LENGTH) {
            ErrorResponse errorResponse = new ErrorResponse();
            if (requestData.getTitle().length() < POST_TITLE_MIN_LENGTH) {
                errorResponse.addError("title", "Заголовок не установлен");
            }
            if (requestData.getText().length() < POST_TEXT_MIN_LENGTH) {
                errorResponse.addError("text", "Текст публикации слишком короткий");
            }
            return errorResponse;
        } else {
            Post post = new Post();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User " + userEmail + " not found"));

            fillPost(post, requestData);
            post.setActive(true);
            post.setModerationStatus(ModerationStatus.NEW);
            post.setAuthor(user);

            postRepository.save(post);
            return new SimpleResponse(true);
        }
    }

    private void fillPost(Post post, PostAddRequest requestData) {
        post.setTitle(requestData.getTitle());
        post.setActive(requestData.isActive());
        post.setText(requestData.getText());
        post.setTime(timestampToUtcDatetime(requestData.getTimestamp()));

        List<Tag> tagsFromRepository = tagRepository.findByNameIn(requestData.getTags());
        post.setTags(tagsFromRepository);
    }

    public void moderatePost(PostStatusModerationData moderationData, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty() || !user.get().isModerator()) {
            throw new SimpleException("User does not exist or is not a moderator");
        } else if (!postRepository.existsById(moderationData.getPostId())) {
            throw new SimpleException(String.format("Post %d does not exist", moderationData.getPostId()));
        }

        Post post = postRepository.findPostById(moderationData.getPostId());
        if (moderationData.getDecision().equals(ModerationStatusRequest.accept)) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.DECLINED);
        }

        postRepository.save(post);
    }

    private LocalDateTime timestampToUtcDatetime(long timestamp) {
        if (LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)
                .isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        } else {
            return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
        }
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

    public PostResponse mapToPostResponse(List<PostUserCounts> postsAdditionalInfo) {
        PostResponse response = new PostResponse();
        response.setCount(postsAdditionalInfo.size());

        List<PostResponse.PostInfo> posts = new ArrayList<>();
        for (PostUserCounts item : postsAdditionalInfo) {
            posts.add(createPostInfo(item));
        }
        response.setPosts(posts);
        return response;
    }

    private PostResponse.PostInfo createPostInfo(PostUserCounts post) {
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

    public BlogStatisticsResponse getMyStatistics(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotAuthorizedException(String.format("User %s not found", email)));
        return postRepository.findMyStatistics(user.getId());
    }

    public BlogStatisticsResponse getCommonStatistics() {
        return postRepository.findCommonStatistics();
    }

    public void votePost(PostVoteData voteData, String email, boolean like) {
        Post post = postRepository.findPostById(voteData.getPostId());
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotAuthorizedException(String.format("User %s not found", email)));


        if (post == null) {
            throw new SimpleException(String.format("Post %d not found", voteData.getPostId()));
        }

        boolean userAlreadyVoted = postVoteRepository.isUserVoted(post.getId(), user.getId());
        LOGGER.info("user voted: {}", userAlreadyVoted);
        if (userAlreadyVoted) {
            throw new SimpleException(String.format("User %s has already voted", email));
        }

        PostVote postVote = new PostVote();
        postVote.setTime(LocalDateTime.now());
        postVote.setPost(post);
        postVote.setValue(like ? 1 : -1);
        postVote.setUser(user);
        postVoteRepository.save(postVote);
    }
}
