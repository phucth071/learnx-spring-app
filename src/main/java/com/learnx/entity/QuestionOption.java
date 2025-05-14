package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.embeddedId.QuestionOptionId;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_option")
@Builder
public class QuestionOption extends Auditable {
    @EmbeddedId
    private QuestionOptionId id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "seq", nullable = false)
    private int seq;

    @JsonBackReference
    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private Question question;
}
