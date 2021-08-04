package com.skillbox.blogengine.service;

import com.skillbox.blogengine.model.custom.TagUsageStatistics;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final TagRepository tagRepository;
    private final PostRepository postRepository;


    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public List<TagUsageStatistics> selectTagsStatistics(String tagPartName) {
        return tagRepository.findTagsByNameWithUsages(tagPartName, Sort.by("useInPostsCount").descending());
    }
}
