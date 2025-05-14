package com.learnx.entity;


import com.learnx.entity.auditing.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quiz_submission_answer")
@Builder
public class QuizSubmissionAnswer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_detail_id", referencedColumnName = "id")
    private QuizSubmissionDetail quizSubmissionDetail;
}
