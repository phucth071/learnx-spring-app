package com.learnx.dto;

import com.learnx.entity.enumClass.State;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDto {
    private Long id;
    private String content;
    private Date startDate;
    private Date endDate;
    private State state;
    private String title;
    private String urlDocument;
    private Long moduleId;
}
