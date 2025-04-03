package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.learnx.entity.auditing.Auditable;
import com.learnx.entity.enumClass.Role;
import com.learnx.entity.enumClass.State;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "change_role_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
