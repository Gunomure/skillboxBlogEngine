package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.dto.MyStatisticsResponse;
import com.skillbox.blogengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);

    @Override
    Optional<User> findById(Integer integer);

    @Query("SELECT new com.skillbox.blogengine.dto.MyStatisticsResponse(" +
            "  COUNT(p.id)                             AS postsCount" +
            " ,COUNT(CASE pv.value WHEN 1 THEN 1 END)  AS likeCount" +
            " ,COUNT(CASE pv.value WHEN -1 THEN 1 END) AS dislikeCount" +
            " ,SUM(p.viewCount)                        AS viewsCount" +
            " ,MIN(UNIX_TIMESTAMP(p.time))             AS firstPublication)" +
            " FROM User u" +
            " LEFT JOIN  u.posts p" +
            " LEFT JOIN p.postVotes pv" +
            " WHERE u.id = :userId")
    MyStatisticsResponse findMyStatistics(int userId);
}
