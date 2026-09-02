package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserPermissionId;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserPermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserPermissionRepository extends JpaRepository<UserPermissionJpaEntity, UserPermissionId> {
}
