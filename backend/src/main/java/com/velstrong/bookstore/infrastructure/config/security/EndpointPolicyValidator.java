package com.velstrong.bookstore.infrastructure.config.security;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EndpointPolicyValidator implements SmartInitializingSingleton {

    private static final Set<String> SUPPORTED_METHODS = Set.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

    private final EndpointSecurityProperties properties;

    public EndpointPolicyValidator(EndpointSecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        validate(properties);
    }

    public static void validate(EndpointSecurityProperties properties) {
        Set<String> routeKeys = new HashSet<>();
        validateRoutes("public", properties.publicRoutes(), false, routeKeys);
        validateRoutes("authenticated", properties.authenticated(), false, routeKeys);
        validateRoutes("permissions", properties.permissions(), true, routeKeys);
    }

    private static void validateRoutes(String group, List<EndpointPolicy> policies,
                                       boolean requiresPermission, Set<String> routeKeys) {
        for (EndpointPolicy policy : policies) {
            validatePolicy(group, policy, requiresPermission);
            if (!routeKeys.add(policy.routeKey())) {
                throw new IllegalStateException("Duplicate endpoint security policy: " + policy.routeKey());
            }
        }
    }

    private static void validatePolicy(String group, EndpointPolicy policy, boolean requiresPermission) {
        if (policy.method() == null || policy.method().isBlank()) {
            throw new IllegalStateException(group + " route is missing method");
        }
        if (!SUPPORTED_METHODS.contains(policy.method())) {
            throw new IllegalStateException(group + " route has invalid HTTP method: " + policy.method());
        }
        try {
            policy.httpMethod();
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(group + " route has invalid HTTP method: " + policy.method(), ex);
        }

        if (policy.path() == null || policy.path().isBlank()) {
            throw new IllegalStateException(group + " route is missing path");
        }
        if (!policy.path().startsWith("/")) {
            throw new IllegalStateException(group + " route path must start with '/': " + policy.path());
        }

        if (!requiresPermission && policy.permission() != null && !policy.permission().isBlank()) {
            throw new IllegalStateException(group + " route must not declare permission: " + policy.routeKey());
        }

        if (requiresPermission) {
            if (policy.permission() == null || policy.permission().isBlank()) {
                throw new IllegalStateException(group + " route is missing permission: " + policy.routeKey());
            }
            if (!KnownPermissions.VALUES.contains(policy.permission())) {
                throw new IllegalStateException(group + " route uses unknown permission: " + policy.permission());
            }
        }
    }
}
