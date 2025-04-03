package com.learnx.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumDto {
    private Long courseId;
    private String description;
    private String title;
}
