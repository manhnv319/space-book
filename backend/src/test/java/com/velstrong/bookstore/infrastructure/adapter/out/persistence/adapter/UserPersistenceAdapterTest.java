package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaPermissionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaRoleRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserRoleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserPersistenceAdapterTest {

    @Test
    void findByUsernameLoadsRolesAndEffectivePermissions() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        JpaRoleRepository roleRepository = mock(JpaRoleRepository.class);
        JpaPermissionRepository permissionRepository = mock(JpaPermissionRepository.class);
        JpaUserRoleRepository userRoleRepository = mock(JpaUserRoleRepository.class);
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(
                userRepository, roleRepository, permissionRepository, userRoleRepository);

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(7L);
        entity.setUsername("admin");
        entity.setPassword("hash");
        entity.setEmail("admin@x");
        entity.setStatus("ACTIVE");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(entity));
        when(roleRepository.findCodesByUserId(7L)).thenReturn(List.of("ADMIN"));
        when(permissionRepository.findRolePermissionCodesByUserId(7L))
                .thenReturn(List.of("voucher:manage", "order:read:all"));
        when(permissionRepository.findDirectPermissionCodesByUserId(any(), any()))
                .thenReturn(List.of("report:view"));

        User user = adapter.findByUsername("admin").orElseThrow();

        assertThat(user.getRoles()).containsExactly("ADMIN");
        assertThat(user.getScopes()).containsExactly("voucher:manage", "order:read:all", "report:view");
    }
}
