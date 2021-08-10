package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.PostComment;
import com.skillbox.blogengine.model.custom.CommentUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComment, Integer> {

    @Query("SELECT new com.skillbox.blogengine.model.custom.CommentUserInfo(pc.id AS id" +
            ", UNIX_TIMESTAMP(pc.time) AS timestamp" +
            ", pc.text AS text" +
            ", u.id AS userId" +
            ", u.name AS userName" +
            ", u.photo AS userPhoto)" +
            " FROM PostComment pc" +
            " JOIN pc.user u" +
            " JOIN pc.post p" +
            " WHERE p.isActive = TRUE AND p.moderationStatus = 'ACCEPTED' AND now() >= p.time" +
            " AND p.id = :postId" +
            " ORDER BY pc.id")
    List<CommentUserInfo> findCommentsByPostId(int postId);
}
