package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.embeddedId.QuizId;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "modules"})
@Table(name = "quiz")
@Builder
public class Quiz extends Auditable {
    @EmbeddedId
    private QuizId id;

    private boolean status;

    @Column(columnDefinition = "nvarchar(255)")
    private String title;

    @Column(columnDefinition = "nvarchar(1000)")
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    @JoinColumn(name = "module_id",
            referencedColumnName = "id", insertable = false,  updatable = false,
            foreignKey = @ForeignKey(name = "FK_module_quizzes"))
    private Module module;

    @OneToMany(mappedBy = "quiz", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();
}
