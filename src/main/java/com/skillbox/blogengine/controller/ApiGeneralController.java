package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.service.GlobalSettingsService;
import com.skillbox.blogengine.service.PostService;
import com.skillbox.blogengine.service.TagService;
import com.skillbox.blogengine.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final static Logger LOGGER = LogManager.getLogger(ApiGeneralController.class);

    private InitResponse initResponse;
    private GlobalSettingsService settingsService;
    private UserService userService;
    private PostService postService;
    private TagService tagService;

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, UserService userService, PostService postService, TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.userService = userService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/init")
    private InitResponse init() {
        System.out.println(initResponse.toString());
        return initResponse;
    }

    @GetMapping("/settings")
    private GlobalSettingsResponse settings() {
        return settingsService.selectAll();
    }

    @GetMapping("/auth/check")
    private UserResponse checkAuth() {
        // TODO раскоментить когда будет авторизация
//        try {
//            User user = userService.getById(1);
//            AuthorizedUser authorizedUser = new AuthorizedUser();
//            return authorizedUser.map(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return new NotAuthorizedUser();
    }

    @GetMapping("/post")
    private PostResponse getPosts(@RequestParam(defaultValue = "0") Integer offset,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(defaultValue = "recent") ModeType mode) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, mode = {}", offset, limit, mode);
        return postService.selectAllPostsByParameters(offset, limit, mode);
    }

    @GetMapping("/post/search")
    private PostResponse getPosts(@RequestParam(defaultValue = "0") Integer offset,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(defaultValue = "") String query) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, query);
        return postService.selectFilteredPostsByParameters(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    private PostResponse getPostsByDate(@RequestParam(defaultValue = "0") Integer offset,
                                        @RequestParam(defaultValue = "10") Integer limit,
                                        @RequestParam(defaultValue = "") String date) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, date);
        return postService.selectPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    private PostResponse getPostsByTag(@RequestParam(defaultValue = "0") Integer offset,
                                        @RequestParam(defaultValue = "10") Integer limit,
                                        @RequestParam(defaultValue = "") String tag) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, tag);
        return postService.selectPostsByTag(offset, limit, tag);
    }

    @GetMapping("/tag")
    private TagResponse getTags(@RequestParam(defaultValue = "") String query) {
        return tagService.selectTagsStatistics(query);
    }

    @GetMapping("/calendar")
    private CalendarResponse getCalendar(@RequestParam(defaultValue = "-1") int year) {
        // TODO возможно есть вариант получше, чем задавать -1
        if (year < 0) {
            year = LocalDate.now().getYear();
        }

        return postService.selectPostsCountsByYear(year);
    }
}
