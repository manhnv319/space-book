package com.velstrong.bookstore.infrastructure.config.security;

import java.util.List;

public record EndpointSecurityProperties(
        List<EndpointPolicy> publicRoutes,
        List<EndpointPolicy> authenticated,
        List<EndpointPolicy> permissions
) {

    public EndpointSecurityProperties {
        publicRoutes = List.copyOf(nullToEmpty(publicRoutes));
        authenticated = List.copyOf(nullToEmpty(authenticated));
        permissions = List.copyOf(nullToEmpty(permissions));
    }

    private static List<EndpointPolicy> nullToEmpty(List<EndpointPolicy> policies) {
        return policies == null ? List.of() : policies;
    }
}
