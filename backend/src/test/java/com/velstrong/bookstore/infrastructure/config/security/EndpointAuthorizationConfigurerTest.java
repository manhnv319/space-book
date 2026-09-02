package com.velstrong.bookstore.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Spring Security applies the first matching rule. Public routes may use wildcards
 * (GET /api/v1/books/**) while permission rules use exact paths, so permission rules
 * must be registered first or a wildcard public route silently exposes them.
 */
class EndpointAuthorizationConfigurerTest {

    private static final String ADMIN_GET_UNDER_PUBLIC_PREFIX = "/api/v1/books/bestseller-suggestions";
    private static final String PUBLIC_WILDCARD = "/api/v1/books/**";

    @Test
    void registersPermissionRulesBeforeWildcardPublicRoutes() {
        EndpointSecurityProperties properties = EndpointSecurityPolicyLoader.loadFromString("""
                security:
                  public:
                    - method: GET
                      path: /api/v1/books/**
                  permissions:
                    - method: GET
                      path: /api/v1/books/bestseller-suggestions
                      permission: book:manage
                """);

        @SuppressWarnings("unchecked")
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry =
                mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS);

        new EndpointAuthorizationConfigurer(properties).apply(registry);

        InOrder order = inOrder(registry);
        order.verify(registry).requestMatchers(HttpMethod.GET, ADMIN_GET_UNDER_PUBLIC_PREFIX);
        order.verify(registry).requestMatchers(HttpMethod.GET, PUBLIC_WILDCARD);
    }

    @Test
    void registersAuthenticatedRulesLast() {
        EndpointSecurityProperties properties = EndpointSecurityPolicyLoader.loadFromString("""
                security:
                  public:
                    - method: GET
                      path: /api/v1/books/**
                  authenticated:
                    - method: GET
                      path: /api/v1/cart
                  permissions:
                    - method: GET
                      path: /api/v1/orders
                      permission: order:read:all
                """);

        @SuppressWarnings("unchecked")
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry =
                mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS);

        new EndpointAuthorizationConfigurer(properties).apply(registry);

        InOrder order = inOrder(registry);
        order.verify(registry).requestMatchers(HttpMethod.GET, "/api/v1/orders");
        order.verify(registry).requestMatchers(HttpMethod.GET, PUBLIC_WILDCARD);
        order.verify(registry).requestMatchers(HttpMethod.GET, "/api/v1/cart");
    }

    @Test
    void everyPermissionPathInShippedPolicyIsExactSoOrderingIsSafe() {
        EndpointSecurityProperties properties = EndpointSecurityPolicyLoader.loadFromString(
                readShippedPolicy());

        properties.permissions().forEach(policy ->
                org.assertj.core.api.Assertions.assertThat(policy.path())
                        .as("permission rule %s must not use a wildcard, otherwise registering "
                                + "permissions first could shadow a public route", policy.path())
                        .doesNotContain("*"));
    }

    private static String readShippedPolicy() {
        try {
            return java.nio.file.Files.readString(
                    java.nio.file.Path.of("src/main/resources/security-endpoints.yml"));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("cannot read security-endpoints.yml", e);
        }
    }
}
