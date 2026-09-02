package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_permissions")
@IdClass(UserPermissionId.class)
@Getter
@Setter
public class UserPermissionJpaEntity {

    @Id
    private Long userId;

    @Id
    private Long permissionId;

    private LocalDateTime expiresAt;
}
