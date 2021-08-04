package com.skillbox.blogengine.model.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagUsageStatistics {
    private String name;
    private long useInPostsCount;
}
