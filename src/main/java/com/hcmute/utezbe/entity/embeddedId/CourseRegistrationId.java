package com.hcmute.utezbe.entity.embeddedId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CourseRegistrationId implements Serializable {
    @Column(name = "student_id")
    private Long student_id;

    @Column(name = "course_id")
    private Long course_id;
}
