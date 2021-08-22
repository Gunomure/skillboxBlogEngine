package com.skillbox.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id лайка/дизлайка'")
    private int id;

    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время лайка / дизлайка'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "TINYINT COMMENT 'лайк или дизлайк: 1 или -1'")
    private boolean value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL COMMENT 'тот, кто поставил лайк / дизлайк'")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id", columnDefinition = "INT NOT NULL COMMENT 'пост, которому поставлен лайк / дизлайк'")
    private Post post;

    @Override
    public String toString() {
        return "PostVote{" +
                "id=" + id +
                ", time=" + time +
                ", value=" + value +
                ", user=" + user.getId() +
                ", post=" + post.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostVote postVote = (PostVote) o;
        return id == postVote.id && value == postVote.value && Objects.equals(time, postVote.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, value);
    }
}
