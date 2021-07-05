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
@Table(name = "tags")
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id тэга'")
    private int id;

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'текст тэга'")
    private String name;
}
