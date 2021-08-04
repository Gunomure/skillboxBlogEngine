package com.skillbox.blogengine.service;

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

    public List<GlobalSetting> selectAll() {
        return new ArrayList<>(settingsRepository.findAll());
    }
}
