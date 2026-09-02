package com.velstrong.bookstore.infrastructure.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EndpointAuthorizationConfigurer {

    private final EndpointSecurityProperties properties;

    public EndpointAuthorizationConfigurer(EndpointSecurityProperties properties) {
        this.properties = properties;
    }

    public void apply(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        // Spring Security uses the first matching rule, so specific rules must be registered
        // before broad ones. Permission rules use exact paths; public routes may use wildcards
        // (e.g. GET /api/v1/books/**). Registering permissions first prevents a wildcard public
        // route from silently making a permission-protected endpoint under the same prefix public.
        for (EndpointPolicy policy : properties.permissions()) {
            auth.requestMatchers(policy.httpMethod(), policy.path()).hasAuthority(policy.permission());
        }
        for (EndpointPolicy policy : properties.publicRoutes()) {
            auth.requestMatchers(policy.httpMethod(), policy.path()).permitAll();
        }
        for (EndpointPolicy policy : properties.authenticated()) {
            auth.requestMatchers(policy.httpMethod(), policy.path()).authenticated();
        }
    }
}
