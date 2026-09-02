package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RolePermissionId;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRolePermissionRepository extends JpaRepository<RolePermissionJpaEntity, RolePermissionId> {
}
