package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.auth.RoleType;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PermissionJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.UserMapper;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Profile("mongodb & !postgres")
public class MongoUserPersistenceAdapter extends MongoPersistenceSupport implements UserRepository {

    private static final String USERS = "users";
    private static final String ROLES = "roles";
    private static final String PERMISSIONS = "permissions";
    private static final String USER_ROLES = "user_roles";
    private static final String ROLE_PERMISSIONS = "role_permissions";
    private static final String USER_PERMISSIONS = "user_permissions";

    private final UserMapper mapper = new UserMapper();

    public MongoUserPersistenceAdapter(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = save(USERS, mapper.toJpaEntity(user));
        syncRoles(saved.getId(), user.getRoles());
        return toDomain(saved);
    }

    @Override public Optional<User> findById(Long id) { return findById(USERS, UserJpaEntity.class, id).map(this::toDomain); }
    @Override public Optional<User> findByUsername(String username) { return findOne(USERS, UserJpaEntity.class, Query.query(Criteria.where("username").is(username))).map(this::toDomain); }
    @Override public Optional<User> findByEmail(String email) { return findOne(USERS, UserJpaEntity.class, Query.query(Criteria.where("email").is(email))).map(this::toDomain); }
    @Override public Optional<User> findByIamId(String iamId) { return findOne(USERS, UserJpaEntity.class, Query.query(Criteria.where("iamId").is(iamId))).map(this::toDomain); }

    @Override
    public Optional<User> findByUsernameOrEmail(String term) {
        return findOne(USERS, UserJpaEntity.class, Query.query(new Criteria().orOperator(
                Criteria.where("username").is(term), Criteria.where("email").is(term)))).map(this::toDomain);
    }

    @Override
    public List<User> findAll(int page, int size) {
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "_id"))
                .limit(size).skip((long) page * size);
        return find(USERS, UserJpaEntity.class, query).stream().map(this::toDomain).toList();
    }

    @Override public long count() { return count(USERS, new Query(), UserJpaEntity.class); }
    @Override public void deleteById(Long id) { deleteById(USERS, id, UserJpaEntity.class); }

    @Override
    public void updatePassword(Long userId, String encodedPassword) {
        updateFirst(USERS, Query.query(Criteria.where("_id").is(userId)),
                new Update().set("password", encodedPassword), UserJpaEntity.class);
    }

    @Override
    public void updateStatus(Long userId, String status) {
        updateFirst(USERS, Query.query(Criteria.where("_id").is(userId)),
                new Update().set("status", status), UserJpaEntity.class);
    }

    @Override public boolean existsByUsername(String username) { return exists(USERS, Query.query(Criteria.where("username").is(username)), UserJpaEntity.class); }
    @Override public boolean existsByEmail(String email) { return exists(USERS, Query.query(Criteria.where("email").is(email)), UserJpaEntity.class); }

    private User toDomain(UserJpaEntity entity) {
        return mapper.toDomain(entity, loadRoleCodes(entity.getId()), loadEffectivePermissions(entity.getId()));
    }

    private List<String> loadRoleCodes(Long userId) {
        List<Long> roleIds = relationIds(USER_ROLES, "userId", "roleId", userId);
        if (roleIds.isEmpty()) return List.of();
        return find(ROLES, RoleJpaEntity.class, Query.query(Criteria.where("_id").in(roleIds))).stream()
                .sorted(java.util.Comparator.comparing(RoleJpaEntity::getCode, String.CASE_INSENSITIVE_ORDER))
                .map(RoleJpaEntity::getCode).toList();
    }

    private List<String> loadEffectivePermissions(Long userId) {
        Set<Long> permissionIds = new LinkedHashSet<>();
        List<Long> roleIds = relationIds(USER_ROLES, "userId", "roleId", userId);
        if (!roleIds.isEmpty()) {
            permissionIds.addAll(mongo.find(Query.query(Criteria.where("roleId").in(roleIds)),
                            Document.class, ROLE_PERMISSIONS).stream()
                    .map(value -> ((Number) value.get("permissionId")).longValue()).toList());
        }
        Criteria direct = new Criteria().andOperator(Criteria.where("userId").is(userId),
                new Criteria().orOperator(Criteria.where("expiresAt").is(null), Criteria.where("expiresAt").gt(LocalDateTime.now())));
        permissionIds.addAll(mongo.find(Query.query(direct), Document.class, USER_PERMISSIONS).stream()
                .map(value -> ((Number) value.get("permissionId")).longValue()).toList());
        if (permissionIds.isEmpty()) return List.of();
        return find(PERMISSIONS, PermissionJpaEntity.class,
                Query.query(Criteria.where("_id").in(permissionIds))).stream()
                .map(PermissionJpaEntity::getCode).distinct().toList();
    }

    private List<Long> relationIds(String collection, String ownerField, String targetField, Long ownerId) {
        return mongo.find(Query.query(Criteria.where(ownerField).is(ownerId)), Document.class, collection).stream()
                .map(value -> ((Number) value.get(targetField)).longValue()).toList();
    }

    private void syncRoles(Long userId, List<String> rawRoleCodes) {
        List<String> roleCodes = normalizeRoleCodes(rawRoleCodes);
        if (roleCodes.isEmpty()) roleCodes = List.of(RoleType.CUSTOMER.name());
        List<RoleJpaEntity> roles = find(ROLES, RoleJpaEntity.class,
                Query.query(Criteria.where("code").in(roleCodes)));
        if (roles.size() != roleCodes.size()) throw new IllegalStateException("Unknown role in user role list: " + roleCodes);

        mongo.remove(Query.query(Criteria.where("userId").is(userId)), USER_ROLES);
        for (RoleJpaEntity role : roles) {
            Document relation = new Document("_id", userId + ":" + role.getId())
                    .append("userId", userId).append("roleId", role.getId())
                    .append("grantedAt", LocalDateTime.now());
            mongo.save(relation, USER_ROLES);
        }
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
