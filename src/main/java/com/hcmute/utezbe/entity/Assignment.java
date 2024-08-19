package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import jakarta.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "module"})
@Table(name = "assignment")
@Builder
public class Assignment extends Auditable{

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

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name="title")
    private String title;

    @Column(name="url_document")
    private String urlDocument;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_assignment_module"))
    private Module module;

}
