package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forum_db")
public class Forum extends Auditable {

    @Column(name="description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name="title", columnDefinition = "TEXT")
    private String title;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_forum_course"))
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "forum", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Topic> topics = new ArrayList<>();

}
