package com.hcmute.utezbe.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistrationUserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
}
