package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<RoleJpaEntity, Long> {
    Optional<RoleJpaEntity> findByCode(String code);
    List<RoleJpaEntity> findByCodeIn(Collection<String> codes);

    @Query("""
            SELECT r.code FROM RoleJpaEntity r
            JOIN UserRoleJpaEntity ur ON ur.roleId = r.id
            WHERE ur.userId = :userId
            ORDER BY r.code
            """)
    List<String> findCodesByUserId(Long userId);
}
