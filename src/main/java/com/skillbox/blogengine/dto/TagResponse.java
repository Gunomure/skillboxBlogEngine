package com.skillbox.blogengine.dto;

import com.skillbox.blogengine.model.custom.TagUsageStatistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TagResponse {

    private static final int ROUNDING_SCALE = 2;
    @Getter
    @Setter
    @AllArgsConstructor
    public static class WeightedTag {
        private String name;
        private double weight;
    }

    List<WeightedTag> tags;

    public static TagResponse map(List<TagUsageStatistics> tagsUsageStatistics, long postsCount) {
        TagResponse tagResponse = new TagResponse();
        List<WeightedTag> tags = new ArrayList<>();

        if (!tagsUsageStatistics.isEmpty()) {
            double maxWeight = (double) tagsUsageStatistics.get(0).getUseInPostsCount() / postsCount;
            double normalizingFactor = 1 / maxWeight;
            for (TagUsageStatistics item : tagsUsageStatistics) {
                double itemWeight = (double) item.getUseInPostsCount() / postsCount;
                double normalizedWeight = itemWeight * normalizingFactor;
                WeightedTag weightedTag = new WeightedTag(item.getName(), round(normalizedWeight));
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
