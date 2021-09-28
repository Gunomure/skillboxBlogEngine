package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final static Logger LOGGER = LogManager.getLogger(ApiGeneralController.class);

    private final InitResponse initResponse;
    private final GlobalSettingsService settingsService;
    private final PostService postService;
    private final TagService tagService;
    private final GeneralService generalService;
    private final PostCommentsService postCommentsService;

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, PostService postService, TagService tagService, GeneralService generalService, PostCommentsService postCommentsService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.postService = postService;
        this.tagService = tagService;
        this.generalService = generalService;
        this.postCommentsService = postCommentsService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public GlobalSettingsData settings() {
        return settingsService.selectAll();
    }

    @GetMapping("/tag")
    public TagResponse getTags(@RequestParam(defaultValue = "") String query) {
        return tagService.selectTagsStatistics(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam(defaultValue = "-1") int year) {
        // TODO возможно есть вариант получше, чем задавать -1
        if (year < 0) {
            year = LocalDate.now().getYear();
        }

        return postService.selectPostsCountsByYear(year);
    }

    @PostMapping(path = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public String uploadImage(Principal principal, @ModelAttribute("image") ImageData image) {
        LOGGER.info("upload image: {}", image.getImage().getOriginalFilename());
        return generalService.saveImage(image);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public AdditionCommentResponse addComment(Principal principal, @RequestBody CommentData commentData) {
        LOGGER.info("Add comment for post {}", commentData.getPostId());
        return postCommentsService.addComment(commentData, principal.getName());
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAnyAuthority({'user:moderate'})")
    public SimpleResponse moderatePostStatus(Principal principal, @RequestBody PostStatusModerationData moderationData) {
        LOGGER.info("Make decision for post {}", moderationData.getPostId());
        postService.moderatePost(moderationData, principal.getName());
        return new SimpleResponse(true);
    }

    @PostMapping("/profile/my")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse updateProfile(Principal principal, @RequestBody ProfileData profileData) {
        LOGGER.info("Update profile for user: {}", principal.getName());
        generalService.updateProfile(principal.getName(), profileData);
        return new SimpleResponse(true);
    }

    @PostMapping(value = "/profile/my", produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse updateProfile(Principal principal,
                                        @RequestParam("name") String name,
                                        @RequestParam("email") String email,
                                        @RequestParam("removePhoto") boolean removePhoto,
                                        @RequestParam(value = "password", defaultValue = "") String password,
                                        @RequestParam("photo") MultipartFile photo) {
        LOGGER.info("Update profile with image for user: {}", principal.getName());
        ProfileData profileData = new ProfileData(email, name, password.isEmpty() ? null : password, removePhoto, null);
        generalService.updateProfile(principal.getName(), profileData, photo);
        return new SimpleResponse(true);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public BlogStatisticsResponse getMyStatistics(Principal principal) {
        return postService.getMyStatistics(principal.getName());
    }

    @GetMapping("/statistics/all")
    public BlogStatisticsResponse getCommonStatistics() {
        return postService.getCommonStatistics();
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public void settings(Principal principal, @RequestBody GlobalSettingsData globalSettingsData) {
        settingsService.updateGlobalSettings(globalSettingsData, principal.getName());
    }
}
