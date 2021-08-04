package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.ModeType;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.orm.PostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostUserCounts> selectByParameters(int offset, int limit, ModeType mode) {
        List<PostUserCounts> posts;
        switch (mode) {
            case popular:
                posts = postRepository
                        .findPostsInfoPageable(PageRequest.of(offset, limit, Sort.by("commentCount")
                                .descending()));
                break;
            case best:
                posts = postRepository
                        .findPostsInfoPageable(PageRequest.of(offset, limit, Sort.by("likeCount")
                                .descending()));
                break;
            case early:
                posts = postRepository
                        .findPostsInfoPageable(PageRequest.of(offset, limit, Sort.by("timestamp")
                                .descending()));
                break;
            default:
//                 recent тоже считается дефолтным
                posts = postRepository
                        .findPostsInfoPageable(PageRequest.of(offset, limit, Sort.by("timestamp")
                                .ascending()));
                break;
        }

        return posts;
    }

    public long count() {
        return postRepository.count();
    }
}
