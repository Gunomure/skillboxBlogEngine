package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.blogengine.model.GlobalSetting;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GlobalSettingsResponse {

    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;

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