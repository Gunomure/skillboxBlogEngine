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
@Table(name = "global_settings")
public class GlobalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id настройки'")
    private int id;

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'системное имя настройки'")
    private String code;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'название настройки'")
    private String name;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'значение настройки'")
    private String value;
}
