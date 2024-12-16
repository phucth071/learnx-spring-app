package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.auditing.Auditable;
import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "module"})
@Table(name = "assignment")
@Builder
public class Assignment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name="content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

//    TODO: Add submit deadline, out of date

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name="title")
    private String title;

    @Column(name="url_document", columnDefinition = "TEXT")
    private String urlDocument;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_assignment_module"))
    private Module module;

    @JsonBackReference
    @OneToMany(mappedBy = "assignment", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<AssignmentSubmission> assignmentSubmissions;

}
