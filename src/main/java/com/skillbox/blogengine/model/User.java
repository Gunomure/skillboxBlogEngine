package com.skillbox.blogengine.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id пользователя'")
    private int id;

    @NotNull
    @Column(name = "is_moderator", columnDefinition = "TINYINT COMMENT 'является ли пользователь модератором (может ли править глобальные настройки сайта и модерировать посты)'")
    private boolean isModerator;
    @NotNull
    @Column(name = "reg_time", columnDefinition = "datetime COMMENT 'дата и время регистрации пользователя'")
    private LocalDateTime regTime;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'имя пользователя'")
    private String name;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'e-mail пользователя'")
    private String email;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'хэш пароля пользователя'")
    private String password;
    @Nullable
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'код для восстановления пароля, может быть NULL'")
    private String code;
    @Nullable
    @Column(columnDefinition = "text COMMENT 'фотография (ссылка на файл), может быть NULL'")
    private String photo;

    @OneToMany
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL COMMENT 'автор поста'")
    private Set<Post> posts;

    @OneToMany(mappedBy = "user")
    private Set<PostVote> postVotes;
}
