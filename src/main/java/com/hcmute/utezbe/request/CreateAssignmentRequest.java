package com.hcmute.utezbe.request;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentRequest {
    private String content;
    private Date startDate;
    private Date endDate;
    private State state;
    private String title;
    private Long moduleId;
}
