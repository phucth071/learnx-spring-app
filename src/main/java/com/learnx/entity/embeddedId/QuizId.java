package com.learnx.entity.embeddedId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class QuizId implements Serializable {
    @Column(name = "id")
    private Long id;

    @Column(name = "module_id")
    private Long module_id;
}
