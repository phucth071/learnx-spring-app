package com.learnx.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDto {
    private String answer;
    private Long quizSubmissionId;
    private Long quizQuestionId;
}
