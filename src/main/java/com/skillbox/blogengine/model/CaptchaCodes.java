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
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT COMMENT 'id каптча'")
    private int id;

    @NotNull
    @Column(columnDefinition = "datetime COMMENT 'дата и время генерации кода капчи'")
    private LocalDateTime time;
    @NotNull
    @Column(columnDefinition = "TINYTEXT COMMENT 'код, отображаемый на картинкке капчи'")
    private String code;
    @NotNull
    @Column(name = "secret_code", columnDefinition = "TINYTEXT COMMENT 'код, передаваемый в параметре'")
    private String secretCode;
}
