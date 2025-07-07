package com.project.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.commons.model.entity.BaseEntity;
import com.project.commons.model.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 3:37 18/05/2025, 2025
 */

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name="m_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String username;
    String password;
    boolean accountExpired;
    boolean accountLocked;
    boolean credentialExpired;
    String firstName;
    String lastName;
    String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "gender_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    GenderEnum gender;
    String resetToken;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "m_user_role_authority",
//            schema = "core_db",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_role_id", referencedColumnName = "role_code")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<UserRole> authorities = new HashSet<>();
}
