package com.skillbox.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@Entity
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id связи'")
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Override
    public String toString() {
        return "TagToPost{" +
                "id=" + id +
                ", post=" + post.getId() +
                ", tag=" + tag.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagToPost tagToPost = (TagToPost) o;
        return id == tagToPost.id && post.getId() == tagToPost.post.getId() && tag.getId() == tagToPost.tag.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, post.getId(), tag.getId());
    }
}
