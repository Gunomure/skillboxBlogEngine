package com.skillbox.blogengine.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Posts {
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
    @Column(name = "moderator_id", columnDefinition = "INT COMMENT 'ID пользователя-модератора, принявшего решение, или NULL'")
    private int moderatorId;
    @NotNull
    @Column(name = "user_id", columnDefinition = "INT COMMENT 'автор поста'")
    private int userId;
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
}
