package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quiz", "module"})
@Table(name = "question_quiz")
@Builder
public class Question extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "question_type")
    private String questionType;

    @ElementCollection
    private List<String> options;

    @ElementCollection
    private List<String> answers;

    @Column(name = "score")
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumns({
            @JoinColumn(name = "quiz_id", foreignKey = @ForeignKey(name = "FK_quiz_question")),
            @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_module_question"))
        }
    )
    private Quiz quiz;

}
