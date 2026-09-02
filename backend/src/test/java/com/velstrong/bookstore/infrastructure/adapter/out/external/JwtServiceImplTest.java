package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceImplTest {

    private final JwtServiceImpl jwtService = new JwtServiceImpl(
            "velstrong-book-store-test-secret-key-2026-long-enough",
            3600,
            7200);

    @Test
    void tokenContainsNormalizedRolesAndPermissions() {
        User user = User.reconstitute(7L, "staff", "hash", "s@x",
                null, null, null, null, null, UserStatus.ACTIVE,
                List.of("ROLE_ADMIN", "SALES_STAFF"),
                List.of("voucher:manage", "rental:checkin"));

        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.extractRoles(token)).containsExactly("ADMIN", "SALES_STAFF");
        assertThat(jwtService.extractPermissions(token)).containsExactly("voucher:manage", "rental:checkin");
        assertThat(jwtService.extractUserId(token)).isEqualTo(7L);
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }
}
