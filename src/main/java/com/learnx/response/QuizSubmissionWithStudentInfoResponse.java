package com.learnx.response;

import com.learnx.entity.QuizSubmission;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class QuizSubmissionWithStudentInfoResponse {
    private String email;
    private QuizSubmission quizSubmission;
}
