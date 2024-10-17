package com.hcmute.utezbe.request;

import com.hcmute.utezbe.entity.enumClass.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleQueueRequest {
    private String email;
    private Role newRole;
    private Role oldRole;
}
