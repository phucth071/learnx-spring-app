package com.learnx.request;

import com.learnx.dto.OutcomeDTO;
import com.learnx.entity.enumClass.State;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;
    private String description;
    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;
    private String startDate;
    private State state;
    private String code;
    private List<OutcomeDTO> outcomes;
}
