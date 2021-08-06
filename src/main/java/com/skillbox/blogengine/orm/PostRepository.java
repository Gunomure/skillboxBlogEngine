package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.model.custom.PostsCountPerDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT new com.skillbox.blogengine.model.custom.PostUserCounts(p.id AS id" +
            "     , UNIX_TIMESTAMP(p.time)            AS timestamp" +
            "     , a.id                              AS userId" +
            "     , a.name                            AS userName" +
            "     , p.title                           AS title" +
            "     , p.text                            AS announce" +
            "     , COUNT(CASE pv.value WHEN 1 THEN 1 ELSE NULL END)  AS likeCount" +
            "     , COUNT(CASE pv.value WHEN -1 THEN 1 ELSE NULL END) AS dislikeCount" +
            "     , COUNT(pc.id)                      AS commentCount" +
            "     , p.viewCount                       AS viewCount)" +
            " FROM Post p" +
            " LEFT JOIN p.comments pc" +
            " LEFT JOIN p.author a" +
            " LEFT JOIN p.postVotes pv" +
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'ACCEPTED' AND now() >= p.time" +
            "     AND lower(p.text) LIKE LOWER(CONCAT('%', trim(:queryToSearch), '%'))" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findPostsInfoPageable(String queryToSearch, Pageable pageable);

    @Query("SELECT new com.skillbox.blogengine.model.custom.PostsCountPerDate(DATE_FORMAT(p.time,'%Y-%m-%d') AS postsDate" +
            ", COUNT(p.id) AS postsCount) FROM Post p" +
            " WHERE year(p.time) = :year" +
            " GROUP BY DATE_FORMAT(p.time,'%Y-%m-%d')")
    List<PostsCountPerDate> findPostsCountPerDateByYear(int year);

    // TODO возможно есть вариант без кастомного Query
    @Query("SELECT DISTINCT year(p.time) FROM Post p")
    List<Integer> findDistinctYears();

}
