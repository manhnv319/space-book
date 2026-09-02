package com.velstrong.bookstore.infrastructure.config;

import com.velstrong.bookstore.domain.port.out.IamTokenRepository;
import com.velstrong.bookstore.domain.port.out.JwtService;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
import com.velstrong.bookstore.infrastructure.config.security.EndpointAuthorizationConfigurer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final IamTokenRepository tokenRepository;
    private final EndpointAuthorizationConfigurer endpointAuthorizationConfigurer;
    private final SessionVersionRepository sessionVersionRepository;

    public SecurityConfig(JwtService jwtService, IamTokenRepository tokenRepository,
                          EndpointAuthorizationConfigurer endpointAuthorizationConfigurer,
                          SessionVersionRepository sessionVersionRepository) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.endpointAuthorizationConfigurer = endpointAuthorizationConfigurer;
        this.sessionVersionRepository = sessionVersionRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                endpointAuthorizationConfigurer.apply(auth);
                auth.anyRequest().authenticated();
            })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // F18: kept as an instance field (not @Bean) so Spring does not also register
    // it as a global filter — registering twice causes the doFilter to run twice
    // per request. The single instance is wired once into the security chain above.
    private final OncePerRequestFilter jwtAuthFilter = new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // F15: reject blacklisted tokens (i.e. logged out but still unexpired)
                if (!tokenRepository.isAccessTokenBlacklisted(token) && jwtService.isTokenValid(token)) {
                    Long userId = jwtService.extractUserId(token);
                    if (jwtService.extractSessionVersion(token) != sessionVersionRepository.currentVersion(userId)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                    request.setAttribute("currentUserId", userId);

                    // F14: permissions are first-class authorities; roles remain as
                    // ROLE_* authorities for compatibility and coarse-grained checks.
                    List<String> roles = jwtService.extractRoles(token);
                    List<String> permissions = jwtService.extractPermissions(token);
                    var authorities = new ArrayList<SimpleGrantedAuthority>();
                    permissions.stream()
                            .filter(p -> p != null && !p.isBlank())
                            .distinct()
                            .map(SimpleGrantedAuthority::new)
                            .forEach(authorities::add);

                    normalizeRoles(roles).stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .forEach(authorities::add);

                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        }

        private Set<String> normalizeRoles(List<String> roles) {
            Set<String> normalized = new LinkedHashSet<>();
            for (String role : roles) {
                if (role == null || role.isBlank()) continue;
                String value = role.trim().toUpperCase();
                if (value.startsWith("ROLE_")) value = value.substring("ROLE_".length());
                normalized.add(value);
            }
            return normalized;
        }
    };
}
