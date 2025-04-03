package com.learnx.dto;

import com.learnx.entity.enumClass.State;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String name;
    private Date startDate;
    private State state;
    private String description;
    private String thumbnail;
    private Long categoryId;
}
