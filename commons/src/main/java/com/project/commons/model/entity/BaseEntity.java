package com.project.commons.model.entity;

/**
 * Created by user on 3:44 18/05/2025, 2025
 */

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5503240218570115982L;
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    String createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    Instant createdDate;

    @LastModifiedBy
    @Column(name = "modified_by")
    String modifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date")
    Instant modifiedDate;

    @Column(name = "is_active")
    boolean isActive;

    @Column(name = "is_deleted")
    boolean isDeleted;

    @PrePersist
    public void prePersist() {
        this.createdDate = Instant.now();
        this.modifiedDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = Instant.now();
    }
    
}
