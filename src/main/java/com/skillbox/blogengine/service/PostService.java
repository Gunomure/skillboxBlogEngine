package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.ModeType;
import com.skillbox.blogengine.dto.PostResponse;
import com.skillbox.blogengine.dto.CalendarResponse;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.model.custom.PostsCountPerDate;
import com.skillbox.blogengine.orm.PostRepository;
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

    private static final int ANNOUNCE_MAX_LENGTH = 150;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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
                pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").descending());
                break;
            default:
//                 recent тоже считается дефолтным
                pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
                break;
        }

        return map(postRepository.findPostsInfoPageable("", pageRequest));
    }

    public PostResponse selectFilteredPostsByParameters(int offset, int limit, String query) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("timestamp").ascending());
        return map(postRepository.findPostsInfoPageable(query, pageRequest));
    }

    public long count() {
        return postRepository.count();
    }

    public CalendarResponse selectPostsCountsByYear(int year) {
        List<PostsCountPerDate> postsCountPerDates = postRepository.findPostsCountPerDateByYear(year);
        List<Integer> distinctByTime = postRepository.findDistinctYears();
        return mapPostsCountsPerYear(distinctByTime, postsCountPerDates);
    }

    private CalendarResponse mapPostsCountsPerYear(List<Integer> years, List<PostsCountPerDate> postsCountPerDates) {
        Map<String, Long> posts = new HashMap<>();
        for (PostsCountPerDate item : postsCountPerDates) {
            posts.put(item.getPostsDate(), item.getPostsCount());
        }
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(years);
        calendarResponse.setPosts(posts);
        return calendarResponse;
    }

    public static PostResponse map(List<PostUserCounts> postsAdditionalInfo) {
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
