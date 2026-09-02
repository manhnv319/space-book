package com.velstrong.bookstore.infrastructure.config.security;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class EndpointSecurityPolicyLoader {

    private static final Set<String> ROUTE_KEYS = Set.of("method", "path");
    private static final Set<String> PERMISSION_ROUTE_KEYS = Set.of("method", "path", "permission");
    private static final Set<String> SECURITY_KEYS = Set.of("public", "authenticated", "permissions");

    @Bean
    public EndpointSecurityProperties endpointSecurityProperties() {
        return load(new ClassPathResource("security-endpoints.yml"));
    }

    public static EndpointSecurityProperties loadFromString(String yaml) {
        return load(new ByteArrayResource(yaml.getBytes(StandardCharsets.UTF_8)));
    }

    public static EndpointSecurityProperties load(Resource resource) {
        YamlMapFactoryBean factory = new YamlMapFactoryBean();
        factory.setResources(resource);
        Map<String, Object> root = factory.getObject();
        if (root == null || !root.containsKey("security")) {
            throw new IllegalStateException("security-endpoints.yml must define a security root");
        }

        Map<String, Object> security = asMap(root.get("security"), "security");
        if (!SECURITY_KEYS.containsAll(security.keySet())) {
            throw new IllegalStateException("security contains unsupported fields: " + security.keySet());
        }
        return new EndpointSecurityProperties(
                routeList(security, "public", false),
                routeList(security, "authenticated", false),
                routeList(security, "permissions", true)
        );
    }

    private static List<EndpointPolicy> routeList(Map<String, Object> security, String key, boolean requiresPermission) {
        Object raw = security.get(key);
        if (raw == null) {
            return List.of();
        }

        List<Object> entries = asList(raw, "security." + key);
        List<EndpointPolicy> policies = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            String location = "security." + key + "[" + i + "]";
            Map<String, Object> entry = asMap(entries.get(i), location);
            Set<String> allowedKeys = requiresPermission ? PERMISSION_ROUTE_KEYS : ROUTE_KEYS;
            if (!allowedKeys.containsAll(entry.keySet())) {
                throw new IllegalStateException(location + " contains unsupported fields: " + entry.keySet());
            }

            String method = asString(entry.get("method"));
            String path = asString(entry.get("path"));
            String permission = requiresPermission ? asString(entry.get("permission")) : null;
            policies.add(new EndpointPolicy(method, path, permission));
        }
        return policies;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value, String location) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalStateException(location + " must be an object");
        }
        return (Map<String, Object>) map;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object value, String location) {
        if (!(value instanceof List<?> list)) {
            throw new IllegalStateException(location + " must be a list");
        }
        return (List<Object>) list;
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
