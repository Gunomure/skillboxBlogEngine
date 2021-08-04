package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.GlobalSettingsResponse;
import com.skillbox.blogengine.model.GlobalSetting;
import com.skillbox.blogengine.orm.GlobalSettingsRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalSettingsService {

    private final static Logger LOGGER = Logger.getLogger(GlobalSettingsService.class);
    private final GlobalSettingsRepository settingsRepository;


    public GlobalSettingsService(GlobalSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public GlobalSettingsResponse selectAll() {
        return map(new ArrayList<>(settingsRepository.findAll()));
    }

    public GlobalSettingsResponse map(List<GlobalSetting> settingList) {
        GlobalSettingsResponse response = new GlobalSettingsResponse();
        for (GlobalSetting setting : settingList) {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    response.setMultiuserMode(parseSettingsValue(setting.getValue()));
                    break;
                case "POST_PREMODERATION":
                    response.setPostPremoderation(parseSettingsValue(setting.getValue()));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    response.setStatisticsIsPublic(parseSettingsValue(setting.getValue()));
                    break;
            }
        }
        return response;
    }

    private boolean parseSettingsValue(String value) {
        return value.equals("YES");
    }
}
