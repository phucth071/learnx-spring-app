package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDto {
    private String content;
    private Date startDate;
    private Date endDate;
    private State state;
    private String title;
    private String urlDocument;
    private Long moduleId;
}
