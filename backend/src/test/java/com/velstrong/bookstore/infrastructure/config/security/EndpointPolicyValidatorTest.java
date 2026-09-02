package com.velstrong.bookstore.infrastructure.config.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EndpointPolicyValidatorTest {

    @Test
    void validPolicyPasses() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(EndpointPolicy.withoutPermission("GET", "/api/v1/books/**")),
                List.of(EndpointPolicy.withoutPermission("GET", "/api/v1/orders/me")),
                List.of(new EndpointPolicy("GET", "/api/v1/orders", "order:read:all"))
        );

        assertThatCode(() -> EndpointPolicyValidator.validate(properties)).doesNotThrowAnyException();
    }

    @Test
    void duplicateRouteAcrossGroupsFails() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(EndpointPolicy.withoutPermission("GET", "/api/v1/orders")),
                List.of(),
                List.of(new EndpointPolicy("GET", "/api/v1/orders", "order:read:all"))
        );

        assertThatThrownBy(() -> EndpointPolicyValidator.validate(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate endpoint security policy");
    }

    @Test
    void invalidMethodFails() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(EndpointPolicy.withoutPermission("FETCH", "/api/v1/books")),
                List.of(),
                List.of()
        );

        assertThatThrownBy(() -> EndpointPolicyValidator.validate(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid HTTP method");
    }

    @Test
    void pathMustStartWithSlash() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(EndpointPolicy.withoutPermission("GET", "api/v1/books")),
                List.of(),
                List.of()
        );

        assertThatThrownBy(() -> EndpointPolicyValidator.validate(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must start with '/'");
    }

    @Test
    void permissionRouteRequiresPermission() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(),
                List.of(),
                List.of(EndpointPolicy.withoutPermission("GET", "/api/v1/orders"))
        );

        assertThatThrownBy(() -> EndpointPolicyValidator.validate(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing permission");
    }

    @Test
    void unknownPermissionFails() {
        EndpointSecurityProperties properties = new EndpointSecurityProperties(
                List.of(),
                List.of(),
                List.of(new EndpointPolicy("GET", "/api/v1/orders", "order:delete:planet"))
        );

        assertThatThrownBy(() -> EndpointPolicyValidator.validate(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unknown permission");
    }
}
