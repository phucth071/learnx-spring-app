package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.entity.enumClass.State;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "module"})
@Table(name = "change_role_queue")
@Builder
public class ChangeRoleQueue extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name="new_role")
    @Enumerated(EnumType.STRING)
    private Role newRole;

    @Column(name="old_role")
    @Enumerated(EnumType.STRING)
    private Role oldRole;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private State status;

    @JsonIgnoreProperties
    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
