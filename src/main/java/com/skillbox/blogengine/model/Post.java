package com.skillbox.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id поста'")
    private int id;

    @NotNull
    @Column(name = "is_active", columnDefinition = "TINYINT COMMENT 'скрыта или активна публикация: 0 или 1'")
    private boolean isActive;
    @NotNull
    @Column(name = "moderation_status", columnDefinition = "enum('NEW','ACCEPTED', 'DECLINED') COMMENT 'статус модерации, по умолчанию значение \"NEW\"'")
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;
    @ManyToOne
    @JoinColumn(name = "moderator_id", columnDefinition = "INT COMMENT 'ID пользователя-модератора, принявшего решение, или NULL'")
    private User moderator;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL COMMENT 'автор поста'")
    private User author;

    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время публикации поста'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'заголовок поста'")
    private String title;
    @NotNull
    @Column(columnDefinition = "text COMMENT 'текст поста'")
    private String text;
    @NotNull
    @Column(name = "view_count", columnDefinition = "INT COMMENT 'количество просмотров поста'")
    private int viewCount;

    @OneToMany(mappedBy = "post")
    private Set<PostComment> comments;


    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<TagToPost> tagToPost;

    @OneToMany(mappedBy = "post")
    private Set<PostVote> postVotes;

    public void setTags(List<Tag> tagsToSet) {
        this.tagToPost.clear();
        for (Tag tag : tagsToSet) {
            TagToPost tagToPost = new TagToPost();
            tagToPost.setPost(this);
            tagToPost.setTag(tag);
            this.tagToPost.add(tagToPost);
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", isActive=" + isActive +
                ", moderationStatus=" + moderationStatus +
                ", moderator=" + moderator.getId() +
                ", author=" + author.getId() +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", viewCount=" + viewCount +
                ", tags=" + tagToPost.size() +
                ", postVotes=" + postVotes.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && isActive == post.isActive && viewCount == post.viewCount && moderationStatus == post.moderationStatus && Objects.equals(moderator, post.moderator) && author.equals(post.author) && time.equals(post.time) && title.equals(post.title) && text.equals(post.text) && Objects.equals(comments.size(), post.comments.size()) && Objects.equals(tagToPost.size(), post.tagToPost.size()) && Objects.equals(postVotes.size(), post.postVotes.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isActive, moderationStatus, moderator, author, time, title, text, viewCount, comments.size(), tagToPost.size(), postVotes.size());
    }
}
