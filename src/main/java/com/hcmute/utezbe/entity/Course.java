package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column(name="name", unique = true)
    private String name;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private State state = State.OPEN;

    @Column(name="thumbnail")
    private String thumbnail;

    @Column(name="description", columnDefinition = "LONGTEXT")
    private String description;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_course_category"))
    private Category category;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @JsonBackReference
    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<CourseRegistration> courseRegistrations = new ArrayList<>();

    @JsonIgnore
    @JsonBackReference
    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Module> modules = new ArrayList<>();

}
