package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.ModeType;
import com.skillbox.blogengine.dto.PostByIdResponse;
import com.skillbox.blogengine.dto.PostResponse;
import com.skillbox.blogengine.service.PostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final static Logger LOGGER = LogManager.getLogger(ApiPostController.class);

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
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
    public PostByIdResponse getPostsById(@PathVariable int ID) {
        LOGGER.info("Get posts with parameters:\nid = {}", ID);
        return postService.selectPostsById(ID);
    }
}
