package com.learnx.dto;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    private Long moduleId;
    private String title;
    private String startDate;
    private String endDate;
    private Integer timeLimit;
    private Integer attemptLimit;
    private String description;
    private boolean isShuffled;
}
