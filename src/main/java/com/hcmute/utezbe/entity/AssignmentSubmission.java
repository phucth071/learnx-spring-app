package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.embeddedId.AssignmentSubmissionId;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student", "assignment"})
@Table(name = "assignment_submission")
@Builder
public class AssignmentSubmission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "score")
    private Double score;

    @Column(name = "text_submission")
    private String textSubmission;

    @Column(name = "file_submission_url")
    private String fileSubmissionUrl;

//    TODO: Add submit date -> update_At

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", foreignKey = @ForeignKey(name = "FK_assignment_submission_assignment"),
    insertable = false, updatable = false)
    @JsonIgnore
    private Assignment assignment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "FK_assignment_submission_account"),
    insertable = false, updatable = false)
    private User student;

}
