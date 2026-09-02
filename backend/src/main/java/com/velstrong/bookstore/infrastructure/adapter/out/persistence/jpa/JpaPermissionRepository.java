package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaPermissionRepository extends JpaRepository<PermissionJpaEntity, Long> {
    Optional<PermissionJpaEntity> findByCode(String code);

    @Query("""
            SELECT DISTINCT p.code FROM PermissionJpaEntity p
            JOIN RolePermissionJpaEntity rp ON rp.permissionId = p.id
            JOIN UserRoleJpaEntity ur ON ur.roleId = rp.roleId
            WHERE ur.userId = :userId
            """)
    List<String> findRolePermissionCodesByUserId(Long userId);

    @Query("""
            SELECT DISTINCT p.code FROM PermissionJpaEntity p
            JOIN UserPermissionJpaEntity up ON up.permissionId = p.id
            WHERE up.userId = :userId
              AND (up.expiresAt IS NULL OR up.expiresAt > :now)
            """)
    List<String> findDirectPermissionCodesByUserId(Long userId, LocalDateTime now);
}
