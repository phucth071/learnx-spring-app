package com.hcmute.utezbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.exception.ApiException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true)
public abstract class Auditable {

    @NotNull
//    Tham chiếu đến Id của Account
//    (tránh trường hợp ID create hoặc modified không tồn tại trong bảng Account)
//    @ManyToOne
//    @JoinColumn(name = "created_by", referencedColumnName = "id")
//    private User createdBy;
    private Long createdBy;
//    @ManyToOne
//    @JoinColumn(name = "created_by", referencedColumnName = "id")
//    private User createdBy;
    private Long updatedBy;
    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var userId = RequestContext.getUserId();
        if (userId == null) {
//            throw new ApiException("Cannot persist entity without userID in Request Context");
            userId = 0L;
        }
        setCreatedAt(LocalDateTime.now());
        setCreatedBy(userId);
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }

    @PreUpdate
    public void preUpdate() {
        var userId = RequestContext.getUserId();
        if (userId == null) {
            //            throw new ApiException("Cannot persist entity without userID in Request Context");
            userId = 0L;
        };
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }
}
