package com.skillbox.blogengine.model;

import com.skillbox.blogengine.model.enums.Role;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @OneToMany(mappedBy = "author")
    private Set<Post> posts;

    @OneToMany(mappedBy = "moderator")
    private Set<Post> postsModerated;

    @OneToMany(mappedBy = "user")
    private Set<PostVote> postVotes;

    public Role getRole() {
        return isModerator ? Role.MODERATOR : Role.USER;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", isModerator=" + isModerator +
                ", regTime=" + regTime +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                ", photo='" + photo + '\'' +
                ", posts=" + posts.size() +
                ", postsModerated=" + postsModerated.size() +
                ", postVotes=" + postVotes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && isModerator == user.isModerator && regTime.equals(user.regTime) && name.equals(user.name) && email.equals(user.email) && password.equals(user.password) && Objects.equals(code, user.code) && Objects.equals(photo, user.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isModerator, regTime, name, email, password, code, photo);
    }
}
