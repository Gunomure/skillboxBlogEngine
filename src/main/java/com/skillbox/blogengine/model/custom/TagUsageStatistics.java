package com.skillbox.blogengine.model.custom;

import lombok.Data;

@Data
public class TagUsageStatistics {
    private String name;
    private long useInPostsCount;
}
