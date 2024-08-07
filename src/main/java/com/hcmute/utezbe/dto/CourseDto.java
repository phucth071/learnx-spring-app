package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Long categoryId;

    private List<UserDto> students;
    private List<ModuleDto> modules;
}
