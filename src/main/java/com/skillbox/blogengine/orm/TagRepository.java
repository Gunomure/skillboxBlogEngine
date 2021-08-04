package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.Tag;
import com.skillbox.blogengine.model.custom.TagUsageStatistics;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("SELECT new com.skillbox.blogengine.model.custom.TagUsageStatistics(t.name AS name, COUNT(p.id) AS useInPostsCount)" +
            " FROM Post p JOIN p.tagToPost t2p join t2p.tag t" +
            " WHERE lower(t.name) LIKE LOWER(CONCAT('%', ?1, '%'))" +
            " group by t.name")
    List<TagUsageStatistics> findTagsByNameWithUsages(String name, Sort sort);
}
