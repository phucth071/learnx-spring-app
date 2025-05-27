package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.enumClass.QuestionType;
import com.learnx.entity.views.QuestionAnswerViews;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(QuestionAnswerViews.Student.class)
@Builder
public class Question extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "question_type")
    private QuestionType questionType;

    @Column(name = "score")
    private Double score;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions;

    @JsonManagedReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<QuestionOption> options;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonView(QuestionAnswerViews.Teacher.class)
    private List<QuestionAnswer> answers;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private List<QuizSubmissionDetail> quizSubmissionDetails;
}
