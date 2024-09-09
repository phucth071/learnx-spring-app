package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.User;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDto {
    private String fullName;

    private String email;

    public static final AuthUserDto convertToDto(User user) {
        return AuthUserDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
