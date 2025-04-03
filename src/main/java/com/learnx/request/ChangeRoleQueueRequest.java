package com.learnx.request;

import com.learnx.entity.enumClass.Role;
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
