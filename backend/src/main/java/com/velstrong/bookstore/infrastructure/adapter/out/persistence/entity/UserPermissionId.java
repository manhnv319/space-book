package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserPermissionId implements Serializable {
    private Long userId;
    private Long permissionId;

    public UserPermissionId() {
    }

    public UserPermissionId(Long userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPermissionId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, permissionId);
    }
}
