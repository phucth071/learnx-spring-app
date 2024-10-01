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

    private String avatar;

    public static final AuthUserDto convertToDto(User user) {
        return AuthUserDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .build();
    }
}
