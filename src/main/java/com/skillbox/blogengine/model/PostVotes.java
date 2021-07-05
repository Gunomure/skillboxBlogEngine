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
@Table(name = "post_votes")
public class PostVotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id лайка/дизлайка'")
    private int id;

    @NotNull
    @Column(name = "user_id", columnDefinition = "INT COMMENT 'тот, кто поставил лайк / дизлайк'")
    private int userId;
    @NotNull
    @Column(name = "post_id", columnDefinition = "INT COMMENT 'пост, которому поставлен лайк / дизлайк'")
    private int postId;
    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время лайка / дизлайка'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "TINYINT COMMENT 'лайк или дизлайк: 1 или -1'")
    private boolean value;
}
