package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.blogengine.model.enums.ModerationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostStatusModerationData {
    @JsonProperty("post_id")
    private int postId;
    private ModerationStatus decision;
}