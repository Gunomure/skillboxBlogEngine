package com.skillbox.blogengine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CalendarResponse {

    private List<Integer> years;
    private Map<String, Long> posts;
}
