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
public class CreateMCQRequest {
    private String content;
    private Long quizId;
    private List<Integer> answers;
    private List<String> options;
    private Double score;
    private Long outcomeId;
}
