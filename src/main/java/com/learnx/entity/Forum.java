package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnx.entity.auditing.Auditable;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forum")
@Builder
public class Forum extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name="description", length = 1000)
    private String description;

    @Column(name="title")
    private String title;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_forum_course"), referencedColumnName = "id")
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "forum", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Topic> topics = new ArrayList<>();
}
