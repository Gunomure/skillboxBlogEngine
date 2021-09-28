package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query("SELECT COUNT(*) > 0 FROM PostVote pv WHERE pv.post.id = :postId AND pv.user.id = :userId")
    boolean isUserVoted(int postId, int userId);

    @Query("FROM PostVote pv WHERE pv.user.id = :userId and pv.post.id = :postId")
    PostVote findByUserAndPostId(int userId, int postId);
}
