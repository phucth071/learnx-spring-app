package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.embeddedId.CourseRegistrationId;
import com.hcmute.utezbe.entity.enumClass.State;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "course", "student"})
@Table(name = "course_registration")
public class CourseRegistration extends Auditable {
    @EmbeddedId
    private CourseRegistrationId id;

    @Column(name = "total_gpa")
    private Double totalGPA = 0.0;

    @Column(name = "state")
    private State state = State.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "course_id",
            foreignKey = @ForeignKey(name = "FK_course_registration_course"),
            insertable = false, updatable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "student_id",
            foreignKey = @ForeignKey(name = "FK_course_registration_account"),
            insertable = false, updatable = false)
    private User student;
}
