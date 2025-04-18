package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.enumClass.State;
import lombok.*;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course")
@Builder
public class Course extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private State state = State.OPEN;

    @Column(name="thumbnail")
    private String thumbnail;

    @Column(name="description", length = 1000)
    private String description;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_course_category"))
    private Category category;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @JsonBackReference
    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<CourseRegistration> courseRegistrations;

    @JsonBackReference
    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Module> modules;

}
