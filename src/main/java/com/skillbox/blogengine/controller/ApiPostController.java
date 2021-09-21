package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.dto.enums.ModeType;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import com.skillbox.blogengine.service.PostService;
import com.skillbox.blogengine.service.TagService;
import com.skillbox.blogengine.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final static Logger LOGGER = LogManager.getLogger(ApiPostController.class);

    private final PostService postService;
    private final UserService userService;
    private final TagService tagService;

    public ApiPostController(PostService postService, UserService userService, TagService tagService) {
        this.postService = postService;
        this.userService = userService;
        this.tagService = tagService;
    }

    @GetMapping("/post")
    public PostResponse getPosts(@RequestParam(defaultValue = "0") Integer offset,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(defaultValue = "recent") ModeType mode) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, mode = {}", offset, limit, mode);
        return postService.selectAllPostsByParameters(offset, limit, mode);
    }

    @GetMapping("/post/search")
    public PostResponse getPosts(@RequestParam(defaultValue = "0") Integer offset,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(defaultValue = "") String query) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, query);
        return postService.selectFilteredPostsByParameters(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    public PostResponse getPostsByDate(@RequestParam(defaultValue = "0") Integer offset,
                                       @RequestParam(defaultValue = "10") Integer limit,
                                       @RequestParam(defaultValue = "") String date) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, date);
        return postService.selectPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    public PostResponse getPostsByTag(@RequestParam(defaultValue = "0") Integer offset,
                                      @RequestParam(defaultValue = "10") Integer limit,
                                      @RequestParam(defaultValue = "") String tag) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, query = {}", offset, limit, tag);
        return postService.selectPostsByTag(offset, limit, tag);
    }

    @GetMapping("/post/{ID}")
    public PostByIdResponse getPostsById(Principal principal, @PathVariable int ID) {
        LOGGER.info("Get posts with parameters:\nid = {}", ID);
        return postService.selectPostsById(ID, principal);
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public PostResponse getMyPosts(Principal principal,
                                   @RequestParam(defaultValue = "0") Integer offset,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam PostStatus status) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, status = {}", offset, limit, status);
        LoggedUserResponse user = userService.getByEmail(principal.getName());
        return postService.selectMyPosts(user.getUser().getId(), offset, limit, status);
    }

    @GetMapping("/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public PostResponse getPostsToModerate(Principal principal,
                                           @RequestParam(defaultValue = "0") Integer offset,
                                           @RequestParam(defaultValue = "10") Integer limit,
                                           @RequestParam ModerationStatus status) {
        LOGGER.info("Get posts with parameters:\noffset = {}, limit = {}, status = {}", offset, limit, status);
        LoggedUserResponse user = userService.getByEmail(principal.getName());
        return postService.selectModerationPosts(user.getUser().getId(), offset, limit, status);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse addPost(Principal principal, @RequestBody PostAddRequest requestData) {
        LOGGER.info(requestData.toString());
        LOGGER.info("Save post into DB");
        return postService.addPost(requestData, principal.getName());
    }

    @PutMapping("/post/{ID}")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse updatePost(Principal principal, @PathVariable int ID, @RequestBody PostAddRequest requestData) {
        LOGGER.info(requestData.toString());
        return postService.updatePost(ID, requestData, principal.getName());
    }

    @PostMapping("/post/like")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse likePost(Principal principal, @RequestBody PostVoteData voteData) {
        postService.votePost(voteData, principal.getName(), true);
        return new SimpleResponse(true);
    }

    @PostMapping("/post/dislike")
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public SimpleResponse dislikePost(Principal principal, @RequestBody PostVoteData voteData) {
        postService.votePost(voteData, principal.getName(), false);
        return new SimpleResponse(true);
    }
}
