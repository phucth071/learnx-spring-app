package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hcmute.utezbe.entity.auditing.Auditable;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "module")
@Builder
public class Module extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resources> resources = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Lecture> lectures = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_course_module"))
    private Course course;

    @JsonIgnore
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quiz> quizzes = new ArrayList<>();

}
