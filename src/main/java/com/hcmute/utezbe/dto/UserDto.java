package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.User;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;

    private String email;

    public static final UserDto convertToDto(User user) {
        return UserDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
