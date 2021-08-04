package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {
}
