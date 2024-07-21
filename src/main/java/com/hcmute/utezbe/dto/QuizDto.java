package com.hcmute.utezbe.dto;

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
    private Date startDate;
    private Date endDate;
    private Integer timeLimit;
    private Integer attemptLimit;
    private String description;

    public static QuizDto convertToDto(QuizDto quiz) {
        return QuizDto.builder()
                .moduleId(quiz.getModuleId())
                .title(quiz.getTitle())
                .startDate(quiz.getStartDate())
                .endDate(quiz.getEndDate())
                .timeLimit(quiz.getTimeLimit())
                .attemptLimit(quiz.getAttemptLimit())
                .description(quiz.getDescription())
                .build();
    }
}
