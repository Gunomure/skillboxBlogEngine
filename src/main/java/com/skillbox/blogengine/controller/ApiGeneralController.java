package com.skillbox.blogengine.controller;

import com.skillbox.blogengine.dto.GlobalSettingsResponse;
import com.skillbox.blogengine.dto.InitResponse;
import com.skillbox.blogengine.dto.NotAuthorizedUser;
import com.skillbox.blogengine.dto.UserResponse;
import com.skillbox.blogengine.service.GlobalSettingsService;
import com.skillbox.blogengine.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    final static Logger LOGGER = Logger.getLogger(ApiGeneralController.class);

    private InitResponse initResponse;
    private GlobalSettingsService settingsService;
    private UserService userService;

    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService settingsService, UserService userService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.userService = userService;
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

}
