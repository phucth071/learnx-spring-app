package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private String name;
    private Date startDate;
    private State state;
//    private String thumbnail;
    private String description;
    private Long categoryId;
}
