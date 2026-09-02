package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserAddressRepository extends JpaRepository<UserAddressJpaEntity, Long> {
    List<UserAddressJpaEntity> findByUserId(Long userId);
}
