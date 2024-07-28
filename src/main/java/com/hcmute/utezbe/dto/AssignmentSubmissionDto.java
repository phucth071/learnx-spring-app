package com.hcmute.utezbe.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionDto {
    private Double score;
    private String textSubmission;
    private String fileSubmissionUrl;
    private String linkSubmission;
    private Long assignmentId;
    private Long studentId;
}
