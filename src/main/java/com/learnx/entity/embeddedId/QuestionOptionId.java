package com.learnx.entity.embeddedId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionId implements Serializable {

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "option_id")
    private Long optionId;

    public QuestionOptionId(Long qId, long oid) {}
}