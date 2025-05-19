package com.learnx.entity.embeddedId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class QuestionOptionId implements Serializable {

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "option_id")
    private String optionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionOptionId that = (QuestionOptionId) o;
        return questionId.equals(that.questionId) && optionId.equals(that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, optionId);
    }
}