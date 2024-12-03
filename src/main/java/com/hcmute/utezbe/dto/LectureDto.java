package com.hcmute.utezbe.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDto {
    private String content;
    private String title;
    private Long moduleId;
}
