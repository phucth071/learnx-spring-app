package com.learnx.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDto {
    private Double score;
    private int totalTimes;
    private int totalCorrects;
    private Long studentId;
}
