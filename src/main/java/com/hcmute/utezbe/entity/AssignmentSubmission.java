package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.embeddedId.AssignmentSubmissionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student", "course"})
@Table(name = "assignment_submission")
public class AssignmentSubmission extends Auditable {
    @EmbeddedId
    private AssignmentSubmissionId id;

    @Column(name = "score")
    private Double score;

    @Column(name = "text_submission", columnDefinition = "LONGTEXT")
    private String textSubmission;

    @Column(name = "file_submission_url")
    private String fileSubmissionUrl;

    @Column(name = "link_submission", columnDefinition = "text")
    private String linkSubmission;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "assignment_id", foreignKey = @ForeignKey(name = "FK_assignment_submission_assignment"),
    insertable = false, updatable = false)
    @JsonIgnore
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "FK_assignment_submission_account"),
    insertable = false, updatable = false)
    private User student;
}
