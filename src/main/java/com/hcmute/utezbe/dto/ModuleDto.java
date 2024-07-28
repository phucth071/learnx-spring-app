package com.hcmute.utezbe.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {
    private String description;
    private String name;
    private Long courseId;
}
