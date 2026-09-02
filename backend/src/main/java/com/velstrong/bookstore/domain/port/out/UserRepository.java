package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String term);
    Optional<User> findByIamId(String iamId);
    List<User> findAll(int page, int size);
    long count();
    void deleteById(Long id);
    void updatePassword(Long userId, String encodedPassword);
    void updateStatus(Long userId, String status);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
