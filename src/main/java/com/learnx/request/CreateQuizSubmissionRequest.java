package com.learnx.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizSubmissionRequest {
    private Long quizId;
    private int totalTimeTakenInSeconds;
    private Map<Long, List<String>> answers;
}
