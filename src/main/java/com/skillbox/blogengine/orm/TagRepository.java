package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Integer> {
}
