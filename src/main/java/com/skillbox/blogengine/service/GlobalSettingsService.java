package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.UserNotAuthorizedException;
import com.skillbox.blogengine.dto.GlobalSettingsData;
import com.skillbox.blogengine.model.GlobalSetting;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.GlobalSettingsRepository;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalSettingsService {

    private final static Logger LOGGER = Logger.getLogger(GlobalSettingsService.class);
    private final GlobalSettingsRepository settingsRepository;
    private final UserRepository userRepository;


    public GlobalSettingsService(GlobalSettingsRepository settingsRepository, UserRepository userRepository) {
        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
    }

    public GlobalSettingsData selectAll() {
        return map(new ArrayList<>(settingsRepository.findAll()));
    }

    public GlobalSettingsData map(List<GlobalSetting> settingList) {
        GlobalSettingsData response = new GlobalSettingsData();
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

    public void updateGlobalSettings(GlobalSettingsData setting, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotAuthorizedException(String.format("User %s not found", email)));
        if (user.isModerator()) {
            List<GlobalSetting> globalSettings = settingsRepository.findAll();
            for (GlobalSetting st : globalSettings) {
                if (st.getCode().equalsIgnoreCase("multiuser_mode"))
                    st.setValue(setting.isMultiuserMode() ? "YES" : "NO");
                if (st.getCode().equalsIgnoreCase("post_premoderation"))
                    st.setValue(setting.isPostPremoderation() ? "YES" : "NO");
                if (st.getCode().equalsIgnoreCase("STATISTICS_IS_PUBLIC"))
                    st.setValue(setting.isStatisticsIsPublic() ? "YES" : "NO");
            }
            settingsRepository.saveAll(globalSettings);
        }
    }
}
