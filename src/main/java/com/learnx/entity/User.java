package com.learnx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.learnx.entity.enumClass.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "full_name")
    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;
    @JsonIgnore
    private String password;
    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;
    private LocalDateTime lastLogin;
    private boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role == null ? "STUDENT" : role.name());
        return Collections.singleton(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConfirmToken> confirmTokens;

    @JsonBackReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics;

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizSubmission> quizSubmissions;

    @JsonBackReference
    @OneToMany(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<AssignmentSubmission> assignmentSubmissions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

}
