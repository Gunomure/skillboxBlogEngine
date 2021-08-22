package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private CaptchaService captchaService;
    private RegisterService registerService;

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, UserService userService, PostService postService, TagService tagService, CaptchaService captchaService, RegisterService registerService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.userService = userService;
        this.postService = postService;
        this.tagService = tagService;
        this.captchaService = captchaService;
        this.registerService = registerService;
    }

    @GetMapping("/init")
    private InitResponse init() {
        System.out.println(initResponse.toString());
        return initResponse;
    }

    @GetMapping("/settings")
//    @PreAuthorize("hasAnyAuthority('user:write')")
    private GlobalSettingsResponse settings() {
        return settingsService.selectAll();
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

    @GetMapping("/post/{ID}")
//    @PreAuthorize("hasAnyAuthority('user:write')")
    private PostByIdResponse getPostsById(@PathVariable int ID) {
        LOGGER.info("Get posts with parameters:\nid = {}", ID);
//        return postService.selectPostsByTag(offset, limit, tag);
        return postService.selectPostsById(ID);
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

    @GetMapping("/auth/captcha")
    private CaptchaResponse getCaptcha() {
        captchaService.deleteExpiredCaptcha();
        return captchaService.createAndSaveCaptcha();
    }

    @PostMapping("/auth/register")
    private RegisterResponse postRegister(@RequestBody UserRegisterData userRegisterData) {
        return registerService.registerUser(userRegisterData);
    }
}
