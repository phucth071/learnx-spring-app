package com.learnx.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFITBRequest {
    private String content;
    private Long quizId;
    private String answerContent;
    private Double score;
    private Long outcomeId;
}
