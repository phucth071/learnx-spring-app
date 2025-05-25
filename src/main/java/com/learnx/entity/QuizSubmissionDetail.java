package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.learnx.entity.auditing.Auditable;
import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quiz_submission_detail")
@Builder
public class QuizSubmissionDetail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id")
    private QuizSubmission quizSubmission;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public Long getQuestionId() {
        return question != null ? question.getId() : null;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "quizSubmissionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizSubmissionAnswer> quizSubmissionAnswers;
}
