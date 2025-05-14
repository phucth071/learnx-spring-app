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
public class CreateSCQRequest {
    private String content;
    private Long quizId;
    private Integer answer;
    private List<String> options;
    private Double score;
}
