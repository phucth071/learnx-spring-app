package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(name = "total_times")
    private int totalTimes;

    @Transient
    private int totalCorrects;

    @OneToMany(mappedBy = "quizSubmission", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<QuizAnswer> answers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "FK_quiz_submission_account"))
    private User student;

}
