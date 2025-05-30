package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.learnx.entity.auditing.Auditable;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
@Table(name = "quiz_submission")
@Builder
public class QuizSubmission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private Double score;

    @Column(name = "total_time_taken_seconds")
    private int totalTimeTakenInSeconds;

    @Transient
    private int totalCorrects;

    @JsonManagedReference
    @OneToMany(mappedBy = "quizSubmission", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<QuizSubmissionDetail> answers = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_quiz_submission_quiz"))
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "FK_quiz_submission_account"))
    private User student;

}
