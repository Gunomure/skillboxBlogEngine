package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.dto.BlogStatisticsResponse;
import com.skillbox.blogengine.model.Post;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.model.custom.PostWithComments;
import com.skillbox.blogengine.model.custom.PostsCountPerDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
            "     AND DATE_FORMAT(p.time,'%Y-%m-%d') = :date " +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findPostsInfoByDate(String date, Pageable pageable);

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
            " JOIN p.tagToPost t2p" +
            " JOIN t2p.tag t" +
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'ACCEPTED' AND now() >= p.time" +
            "     AND t.name = :tag" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findPostsInfoByTag(String tag, Pageable pageable);

    @Query("SELECT new com.skillbox.blogengine.model.custom.PostWithComments(p.id AS id" +
            "     , UNIX_TIMESTAMP(p.time)            AS timestamp" +
            "     , p.isActive                        AS active" +
            "     , a.id                              AS userId" +
            "     , a.name                            AS userName" +
            "     , p.title                           AS title" +
            "     , p.text                            AS text" +
            "     , COUNT(CASE pv.value WHEN 1 THEN 1 ELSE NULL END)  AS likeCount" +
            "     , COUNT(CASE pv.value WHEN -1 THEN 1 ELSE NULL END) AS dislikeCount" +
            "     , p.viewCount                       AS viewCount)" +
            " FROM Post p" +
            " LEFT JOIN p.comments pc" +
            " LEFT JOIN p.author a" +
            " LEFT JOIN p.postVotes pv" +
            " WHERE p.id = :id" +
            " GROUP BY p.id, pc.post, pv.post")
    PostWithComments findPostInfoById(int id);

    @Query("SELECT new com.skillbox.blogengine.model.custom.PostsCountPerDate(DATE_FORMAT(p.time,'%Y-%m-%d') AS postsDate" +
            ", COUNT(p.id) AS postsCount) FROM Post p" +
            " WHERE year(p.time) = :year" +
            " GROUP BY DATE_FORMAT(p.time,'%Y-%m-%d')")
    List<PostsCountPerDate> findPostsCountPerDateByYear(int year);

    Post findPostById(int id);

    Post save(Post post);

    // TODO возможно есть вариант без кастомного Query
    @Query("SELECT DISTINCT year(p.time) FROM Post p")
    List<Integer> findDistinctYears();

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
            " WHERE p.isActive = FALSE" +
            "     AND a.id = :userId" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findInactivePosts(int userId, Pageable pageable);

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
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'NEW'" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findPendingPosts(int userId, Pageable pageable);

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
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'DECLINED'" +
            "     AND p.moderator.id = :userId" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findDeclinedPosts(int userId, Pageable pageable);

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
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'ACCEPTED'" +
            "     AND p.moderator.id = :userId" +
            " GROUP BY p.id, pc.post, pv.post")
    List<PostUserCounts> findPublishedPosts(int userId, Pageable pageable);

    @Query("SELECT COUNT(*) FROM Post p WHERE p.moderationStatus = 'NEW' AND p.isActive = TRUE AND p.moderator is NULL")
    int findPostsNeedToModerate();

    @Query("SELECT new com.skillbox.blogengine.dto.BlogStatisticsResponse(" +
            "  COUNT(p.id)                             AS postsCount" +
            " ,COUNT(CASE pv.value WHEN 1 THEN 1 END)  AS likeCount" +
            " ,COUNT(CASE pv.value WHEN -1 THEN 1 END) AS dislikeCount" +
            " ,SUM(p.viewCount)                        AS viewsCount" +
            " ,MIN(UNIX_TIMESTAMP(p.time))             AS firstPublication)" +
            " FROM Post p" +
            " LEFT JOIN p.postVotes pv" +
            " WHERE p.author.id = :userId")
    BlogStatisticsResponse findMyStatistics(int userId);

    @Query("SELECT new com.skillbox.blogengine.dto.BlogStatisticsResponse(" +
            "  COUNT(distinct p.id)                    AS postsCount" +
            " ,COUNT(CASE pv.value WHEN 1 THEN 1 END)  AS likeCount" +
            " ,COUNT(CASE pv.value WHEN -1 THEN 1 END) AS dislikeCount" +
            " ,SUM(p.viewCount)                        AS viewsCount" +
            " ,MIN(UNIX_TIMESTAMP(p.time))             AS firstPublication)" +
            " FROM Post p" +
            " LEFT JOIN p.postVotes pv")
    BlogStatisticsResponse findCommonStatistics();
}
