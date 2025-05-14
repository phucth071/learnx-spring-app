package com.learnx.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDto {
    private List<String> answer;
    private Long quizSubmissionId;
    private Long quizQuestionId;
}
