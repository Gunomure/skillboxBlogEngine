package com.skillbox.blogengine.orm;

import com.skillbox.blogengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);

    @Override
    Optional<User> findById(Integer integer);
}
