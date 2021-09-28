package com.skillbox.blogengine.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileData {
    private String email;
    private String name;
    private String password;
    private boolean removePhoto;
    private String photo;
}