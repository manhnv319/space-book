package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUsername(String username);
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByIamId(String iamId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.username = :term OR u.email = :term")
    Optional<UserJpaEntity> findByUsernameOrEmail(String term);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(Long userId, String password);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.status = :status WHERE u.id = :userId")
    void updateStatus(Long userId, String status);
}
