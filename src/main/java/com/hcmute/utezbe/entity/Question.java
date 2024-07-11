package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "modules"})
@Table(name = "question_quiz")
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(columnDefinition = "varchar(1000)")
    private String content;
    @Column(name = "question_type")
    private String questionType;
    @ElementCollection
    private List<String> options;
    @ElementCollection
    private List<String> answers;
    private double score;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumns({
            @JoinColumn(name = "quiz_id", foreignKey = @ForeignKey(name = "FK_quiz_questions")),
            @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_module_quiz_questions"))
    }
    )
    private Quiz quiz;
}
