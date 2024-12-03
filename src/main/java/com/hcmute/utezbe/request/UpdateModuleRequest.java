package com.hcmute.utezbe.request;

import lombok.*;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModuleRequest {
    @Nullable
    private String description;

    @Nullable
    private String name;
}
