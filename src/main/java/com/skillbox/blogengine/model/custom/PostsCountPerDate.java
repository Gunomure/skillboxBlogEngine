package com.skillbox.blogengine.model.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostsCountPerDate {

    private String postsDate;
    private long postsCount;
}
