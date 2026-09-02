package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@IdClass(UserRoleId.class)
@Getter
@Setter
public class UserRoleJpaEntity {

    @Id
    private Long userId;

    @Id
    private Long roleId;

    private Long grantedBy;
    private LocalDateTime grantedAt;

    public static UserRoleJpaEntity grant(Long userId, Long roleId) {
        UserRoleJpaEntity entity = new UserRoleJpaEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        entity.setGrantedAt(LocalDateTime.now());
        return entity;
    }
}
