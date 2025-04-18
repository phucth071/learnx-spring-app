package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.enumClass.State;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "course", "student"})
@Table(name = "course_registration")
public class CourseRegistration extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_gpa")
    private Double totalGPA = 0.0;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String email;
}
