package com.hcmute.utezbe.response;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentWithCourseId {
    private Long id;
    private String content;
    private Date startDate;
    private Date endDate;
    private State state;
    private String title;
    private String urlDocument;
    private Long moduleId;
    private Long courseId;
    private String courseName;
}
