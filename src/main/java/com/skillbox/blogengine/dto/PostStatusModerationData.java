package com.skillbox.blogengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.blogengine.dto.enums.ModerationStatusRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostStatusModerationData {
    @JsonProperty("post_id")
    private int postId;
    private ModerationStatusRequest decision;
}