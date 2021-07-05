package com.skillbox.blogengine.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id связи'")
    private int id;

    @NotNull
    @Column(name = "post_id", columnDefinition = "INT COMMENT 'id поста'")
    private int postId;
    @NotNull
    @Column(name = "tag_id", columnDefinition = "INT COMMENT 'id тэга'")
    private int tagId;
}
