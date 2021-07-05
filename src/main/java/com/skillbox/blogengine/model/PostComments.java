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
@Table(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id комментария'")
    private int id;

    @Column(name = "parent_id", columnDefinition = "INT COMMENT 'комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)'")
    private int parentId;
    @NotNull
    @Column(name = "post_id", columnDefinition = "INT COMMENT 'пост, к которому написан комментарий'")
    private int postId;
    @NotNull
    @Column(name = "user_id", columnDefinition = "INT COMMENT 'автор комментария'")
    private int userId;
    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время комментария'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "text COMMENT 'текст комментария'")
    private String text;
}
