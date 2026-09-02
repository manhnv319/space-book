package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserJpaEntity;

import java.util.List;

public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        return toDomain(entity, List.of(), List.of());
    }

    public User toDomain(UserJpaEntity entity, List<String> roles, List<String> permissions) {
        return User.reconstitute(
                entity.getId(), entity.getUsername(), entity.getPassword(),
                entity.getEmail(), entity.getFullname(), entity.getPhone(),
                entity.getBirthday(), entity.getIamId(), entity.getCustomerTierId(),
                UserStatus.valueOf(entity.getStatus()), roles, permissions
        );
    }

    public UserJpaEntity toJpaEntity(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        entity.setFullname(domain.getFullname());
        entity.setPhone(domain.getPhone());
        entity.setBirthday(domain.getBirthday());
        entity.setIamId(domain.getIamId());
        entity.setCustomerTierId(domain.getCustomerTierId());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : UserStatus.ACTIVE.name());
        return entity;
    }
}
