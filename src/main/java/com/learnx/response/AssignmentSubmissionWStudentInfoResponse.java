package com.learnx.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentSubmissionWStudentInfoResponse {
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Double score;
    private String textSubmission;
    private String fileSubmissionUrl;
    private String linkSubmission;
    private String createdAt;
    private String updatedAt;
}
