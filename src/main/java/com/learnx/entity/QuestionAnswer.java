package com.learnx.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.learnx.entity.auditing.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_answer")
@Builder
public class QuestionAnswer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private Long answerId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_question_answer_question"))
    private Question question;
}
