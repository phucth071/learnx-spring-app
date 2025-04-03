package com.learnx.request;

import com.learnx.entity.enumClass.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentRequest {
    private String content;
    private String startDate;
    private String endDate;
    private State state;
    private String title;
    private Long moduleId;
}
