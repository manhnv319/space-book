package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.auth.RoleType;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserRoleJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaPermissionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaRoleRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserRoleRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.UserMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Profile("postgres & !mongodb")
public class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final JpaRoleRepository roleRepository;
    private final JpaPermissionRepository permissionRepository;
    private final JpaUserRoleRepository userRoleRepository;
    private final UserMapper mapper;

    public UserPersistenceAdapter(JpaUserRepository jpaRepository,
                                  JpaRoleRepository roleRepository,
                                  JpaPermissionRepository permissionRepository,
                                  JpaUserRoleRepository userRoleRepository) {
        this.jpaRepository = jpaRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.mapper = new UserMapper();
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        syncRoles(saved.getId(), user.getRoles());
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String term) {
        return jpaRepository.findByUsernameOrEmail(term).map(this::toDomain);
    }

    @Override
    public Optional<User> findByIamId(String iamId) {
        return jpaRepository.findByIamId(iamId).map(this::toDomain);
    }

    @Override
    public List<User> findAll(int page, int size) {
        return jpaRepository.findAll(PageRequest.of(page, size))
                .map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String encodedPassword) {
        jpaRepository.updatePassword(userId, encodedPassword);
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, String status) {
        jpaRepository.updateStatus(userId, status);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private User toDomain(UserJpaEntity entity) {
        List<String> roles = roleRepository.findCodesByUserId(entity.getId());
        List<String> permissions = loadEffectivePermissions(entity.getId());
        return mapper.toDomain(entity, roles, permissions);
    }

    private List<String> loadEffectivePermissions(Long userId) {
        Set<String> permissions = new LinkedHashSet<>();
        permissions.addAll(permissionRepository.findRolePermissionCodesByUserId(userId));
        permissions.addAll(permissionRepository.findDirectPermissionCodesByUserId(userId, LocalDateTime.now()));
        return List.copyOf(permissions);
    }

    private void syncRoles(Long userId, List<String> rawRoleCodes) {
        List<String> roleCodes = normalizeRoleCodes(rawRoleCodes);
        if (roleCodes.isEmpty()) roleCodes = List.of(RoleType.CUSTOMER.name());

        List<RoleJpaEntity> roles = roleRepository.findByCodeIn(roleCodes);
        if (roles.size() != roleCodes.size()) {
            throw new IllegalStateException("Unknown role in user role list: " + roleCodes);
        }

        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.saveAll(roles.stream()
                .map(role -> UserRoleJpaEntity.grant(userId, role.getId()))
                .toList());
    }

    private List<String> normalizeRoleCodes(List<String> rawRoleCodes) {
        if (rawRoleCodes == null || rawRoleCodes.isEmpty()) return List.of();
        List<String> result = new ArrayList<>();
        for (String raw : rawRoleCodes) {
            if (raw == null || raw.isBlank()) continue;
            String role = raw.trim().toUpperCase();
            if (role.startsWith("ROLE_")) role = role.substring("ROLE_".length());
            result.add(role);
        }
        return result.stream().distinct().toList();
    }
}
