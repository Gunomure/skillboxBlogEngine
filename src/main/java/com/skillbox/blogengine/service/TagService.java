package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.TagResponse;
import com.skillbox.blogengine.model.Tag;
import com.skillbox.blogengine.model.custom.TagUsageStatistics;
import com.skillbox.blogengine.orm.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    private final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final TagRepository tagRepository;
    private final PostService postService;

    private static final int ROUNDING_SCALE = 2;

    public TagService(TagRepository tagRepository, PostService postService) {
        this.tagRepository = tagRepository;
        this.postService = postService;
    }

    public TagResponse selectTagsStatistics(String tagPartName) {
        long postsCount = postService.count();
        List<TagUsageStatistics> tagUsageStatistics = tagRepository.findTagsByNameWithUsages(tagPartName, Sort.by("useInPostsCount").descending());
        return map(tagUsageStatistics, postsCount);
    }

    public void addTags(List<String> tags) {
        for (String tag : tags) {
            addTag(tag);
        }
    }

    public void addTag(String tag) {
        Tag newTag = new Tag();
        newTag.setName(tag);
        tagRepository.save(newTag);
    }

    public static TagResponse map(List<TagUsageStatistics> tagsUsageStatistics, long postsCount) {
        TagResponse tagResponse = new TagResponse();
        List<TagResponse.WeightedTag> tags = new ArrayList<>();

        if (!tagsUsageStatistics.isEmpty()) {
            double maxWeight = (double) tagsUsageStatistics.get(0).getUseInPostsCount() / postsCount;
            double normalizingFactor = 1 / maxWeight;
            for (TagUsageStatistics item : tagsUsageStatistics) {
                double itemWeight = (double) item.getUseInPostsCount() / postsCount;
                double normalizedWeight = itemWeight * normalizingFactor;
                TagResponse.WeightedTag weightedTag = new TagResponse.WeightedTag(item.getName(), round(normalizedWeight));
                tags.add(weightedTag);
            }

            tagResponse.setTags(tags);
        }

        return tagResponse;
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
