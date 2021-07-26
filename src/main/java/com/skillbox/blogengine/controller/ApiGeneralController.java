package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.model.custom.TagUsageStatistics;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    final static Logger LOGGER = LogManager.getLogger(ApiGeneralController.class);

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
        GlobalSettingsResponse response = new GlobalSettingsResponse();
        return response.map(settingsService.selectAll());
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
    private PostResponse getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam ModeType mode) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, mode = {}", offset, limit, mode);
        return PostResponse.map(postService.selectByParameters(
                Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(10),
                Optional.ofNullable(mode).orElse(ModeType.recent)));
    }

    @GetMapping("/tag")
    private TagResponse getTags() {
        LOGGER.info("Search tags with!!!!!!!!!!!!!!!!");
        List<TagUsageStatistics> tagUsageStatistics = tagService.selectTagsStatistics("");
        long postsCount = postService.count();

        return TagResponse.map(tagUsageStatistics, postsCount);
    }
}
