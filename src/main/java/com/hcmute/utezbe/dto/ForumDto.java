package com.hcmute.utezbe.dto;

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
