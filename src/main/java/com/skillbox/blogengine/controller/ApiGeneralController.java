package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.CalendarResponse;
import com.skillbox.blogengine.dto.GlobalSettingsResponse;
import com.skillbox.blogengine.dto.InitResponse;
import com.skillbox.blogengine.dto.TagResponse;
import com.skillbox.blogengine.service.GlobalSettingsService;
import com.skillbox.blogengine.service.PostService;
import com.skillbox.blogengine.service.TagService;
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

    private final InitResponse initResponse;
    private final GlobalSettingsService settingsService;
    private final PostService postService;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, PostService postService, TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
//    @PreAuthorize("hasAuthority('user:write')")
    public GlobalSettingsResponse settings() {
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
}
