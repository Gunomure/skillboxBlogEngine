package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

    void deleteByTimeBefore(LocalDateTime time);
    CaptchaCode findCaptchaCodeBySecretCode(String secretCode);
}
