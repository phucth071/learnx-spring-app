package com.learnx.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizWithCourseId {
    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean shuffled;
    private Integer timeLimit;
    private Long moduleId;
    private Long courseId;
    private String courseName;
}
