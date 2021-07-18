package com.skillbox.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
}
