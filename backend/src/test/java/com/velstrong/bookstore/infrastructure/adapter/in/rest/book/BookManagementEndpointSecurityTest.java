package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.out.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * F02: proves the D15 route/permission wiring end to end through the real
 * security filter chain — {@code GET /api/v1/books/bestseller-suggestions} and
 * {@code PUT /api/v1/books/{id}/flags} live under the same prefix as the public
 * {@code GET /api/v1/books/**} rule, so this is the exact scenario
 * {@link com.velstrong.bookstore.infrastructure.config.security.EndpointAuthorizationConfigurer}
 * must get right.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookManagementEndpointSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void bestsellerSuggestionsRejectsRequestWithoutToken() throws Exception {
        // Proves the permission rule (registered before the public wildcard, D15)
        // is what rejects this request — not some unrelated 404/500.
        mockMvc.perform(get("/api/v1/books/bestseller-suggestions"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateFlagsRejectsUserWithoutBookManagePermission() throws Exception {
        String token = jwtService.generateAccessToken(userWithScopes(List.of()));

        mockMvc.perform(put("/api/v1/books/999999/flags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isFeatured\":true,\"isBestseller\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateFlagsReturnsNotFoundForUnknownBookWhenAuthorized() throws Exception {
        String token = jwtService.generateAccessToken(userWithScopes(List.of("book:manage")));

        mockMvc.perform(put("/api/v1/books/999999999/flags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isFeatured\":true,\"isBestseller\":true}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void bestsellerSuggestionsSucceedsAndRunsAggregateQueryForAuthorizedUser() throws Exception {
        // Also exercises the real JPQL aggregate against Postgres (theta-join on
        // OrderJpaEntity), catching field-name typos that only surface at runtime.
        String token = jwtService.generateAccessToken(userWithScopes(List.of("book:manage")));

        mockMvc.perform(get("/api/v1/books/bestseller-suggestions?limit=5&days=90")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private User userWithScopes(List<String> scopes) {
        return User.reconstitute(999999L, "security-test", "hash", "security-test@example.com",
                "Security Test", null, null, null, (byte) 1, UserStatus.ACTIVE,
                List.of("CUSTOMER"), scopes);
    }
}
