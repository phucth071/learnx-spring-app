package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "modules"})
@Table(name = "assignment_db")
public class Assignment extends Auditable{

    @Column(name="content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="state")
    private int state; // 1 created, 2 started, 3 expired

    @Column(name="title")
    private String title;

    @Column(name="url_document")
    private String url_document;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "modules_id", foreignKey = @ForeignKey(name = "FK_assignment_modules"))
    private Module modules;

}
