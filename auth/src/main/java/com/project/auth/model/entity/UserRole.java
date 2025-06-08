package com.project.auth.model.entity;

import com.project.commons.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Created by user on 22:11 18/05/2025, 2025
 */

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name="m_user_role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_code")
    String roleCode;
    String roleName;
    String description;
}
