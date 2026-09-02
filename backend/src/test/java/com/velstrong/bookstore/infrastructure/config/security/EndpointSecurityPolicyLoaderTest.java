package com.velstrong.bookstore.infrastructure.config.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EndpointSecurityPolicyLoaderTest {

    @Test
    void loadsYamlPolicy() {
        EndpointSecurityProperties properties = EndpointSecurityPolicyLoader.loadFromString("""
                security:
                  public:
                    - method: GET
                      path: /api/v1/books/**
                  authenticated:
                    - method: GET
                      path: /api/v1/orders/me
                  permissions:
                    - method: GET
                      path: /api/v1/orders
                      permission: order:read:all
                """);

        assertThat(properties.publicRoutes()).hasSize(1);
        assertThat(properties.authenticated()).hasSize(1);
        assertThat(properties.permissions()).hasSize(1);
        assertThat(properties.permissions().getFirst().permission()).isEqualTo("order:read:all");
    }

    @Test
    void rejectsRoleBasedRouteFields() {
        assertThatThrownBy(() -> EndpointSecurityPolicyLoader.loadFromString("""
                security:
                  permissions:
                    - method: GET
                      path: /api/v1/orders
                      role: admin
                """))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unsupported fields");
    }

    @Test
    void rejectsUnsupportedTopLevelSecurityFields() {
        assertThatThrownBy(() -> EndpointSecurityPolicyLoader.loadFromString("""
                security:
                  white-list:
                    username:
                      - admin
                """))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unsupported fields");
    }
}
