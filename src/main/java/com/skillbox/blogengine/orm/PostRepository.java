package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<User, Integer> {
}
