package com.learnx.request;

import com.learnx.dto.OutcomeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCloneRequest {
    private String name;
    private String categoryName;
    private String startDate;
    private String description;
    private String code;
    private List<OutcomeDTO> outcomes;
    private Long courseId;
}