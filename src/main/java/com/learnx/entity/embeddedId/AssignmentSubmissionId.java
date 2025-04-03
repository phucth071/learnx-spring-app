package com.learnx.entity.embeddedId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AssignmentSubmissionId implements Serializable {
    @Column(name = "assignment_id")
    private Long assignment_id;

    @Column(name = "student_id")
    private Long student_id;
}
