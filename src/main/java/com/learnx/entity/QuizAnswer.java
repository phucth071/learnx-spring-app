package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.learnx.entity.auditing.Auditable;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quizQuestion", "quizSubmission"})
@Table(name = "quiz_answer")
@Builder
public class QuizAnswer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id")
    private QuizSubmission quizSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id")
    private Question quizQuestion;

    @Column(name = "answer")
    private String answer;

}
