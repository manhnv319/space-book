package com.velstrong.bookstore.domain.port.out;

/**
 * F20: driven port for password hashing. Implementations live under
 * {@code infrastructure.adapter.out.external}.
 */
public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
