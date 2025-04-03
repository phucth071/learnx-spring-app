package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.embeddedId.QuizId;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "module"})
@Table(name = "quiz")
@Builder
public class Quiz extends Auditable {

    @EmbeddedId
    private QuizId id;

    private boolean status;

    private String title;

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name="time_limit")
    private int timeLimit;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="attempt_allowed")
    private int attemptAllowed;

    @Transient
    private int totalQuestions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "module_id",
            referencedColumnName = "id", insertable = false,  updatable = false,
            foreignKey = @ForeignKey(name = "FK_module_quizzes"))
    private Module module;

    @OneToMany(mappedBy = "quiz", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

}
