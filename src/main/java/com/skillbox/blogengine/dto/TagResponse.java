package com.skillbox.blogengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TagResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WeightedTag {
        private String name;
        private double weight;
    }

    List<WeightedTag> tags;
}
