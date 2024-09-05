package com.hcmute.utezbe.dto;

import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.entity.enumClass.Role;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String fullName;
    private String email;
    private String avatar;
    private Role role;

    public static UserDto convertToDto(User user) {
        return UserDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }
}
