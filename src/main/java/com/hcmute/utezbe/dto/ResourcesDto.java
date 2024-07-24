package com.hcmute.utezbe.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesDto {
    private String title;
    private String urlDocument;
    private Long moduleId;
}
