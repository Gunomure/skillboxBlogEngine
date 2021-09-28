package com.skillbox.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id комментария'")
    private int id;

    @ManyToOne
    @JoinColumn(name = "parent_id", columnDefinition = "INT COMMENT 'комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)'")
    private PostComment parent;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id", columnDefinition = "INT COMMENT 'пост, к которому написан комментарий'")
    private Post post;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT COMMENT 'автор комментария'")
    private User user;
    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время комментария'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "text COMMENT 'текст комментария'")
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostComment that = (PostComment) o;
        return id == that.id && Objects.equals(parent, that.parent) && time.equals(that.time) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parent, time, text);
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "id=" + id +
                ", parent=" + (parent == null ? null : parent.getId()) +
                ", post=" + post.getId() +
                ", user=" + user.getId() +
                ", time=" + time +
                ", text='" + text + '\'' +
                '}';
    }
}
