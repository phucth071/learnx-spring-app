package com.hcmute.utezbe.request;

import com.hcmute.utezbe.entity.enumClass.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    private String name;
    private String description;
    private String categoryName;
    private String startDate;
    private State state;
}
