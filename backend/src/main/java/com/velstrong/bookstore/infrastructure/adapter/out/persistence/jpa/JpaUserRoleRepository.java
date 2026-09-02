package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserRoleId;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserRoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRoleRepository extends JpaRepository<UserRoleJpaEntity, UserRoleId> {
    void deleteByUserId(Long userId);
}
