package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.*;
import com.skillbox.blogengine.service.GeneralService;
import com.skillbox.blogengine.service.GlobalSettingsService;
import com.skillbox.blogengine.service.PostService;
import com.skillbox.blogengine.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, PostService postService, TagService tagService, GeneralService generalService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.postService = postService;
        this.tagService = tagService;
        this.generalService = generalService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
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

    @PostMapping(path = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority({'user:write', 'user:moderate'})")
    public String uploadImage(Principal principal, @ModelAttribute("image") ImageData image) {
        // TODO возвращать error если файл слишком большой + тест на это
        LOGGER.info("user: {}", principal.getName());
        LOGGER.info("uploadImage: {}", image.getImage().getOriginalFilename());
        return generalService.saveImage(image);
    }
}
