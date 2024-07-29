package com.hcmute.utezbe.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private String content;
    private String questionType;
    private List<String> options;
    private List<String> answers;
    private Double score;
    private Long quizId;
    private Long moduleId;
}
