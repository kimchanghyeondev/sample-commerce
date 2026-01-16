package com.toycommerce.gateway.config;

import com.toycommerce.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // 인증 API (공개)
                        .pathMatchers("/api/auth/**").permitAll()
                        // 헬스체크 (공개)
                        .pathMatchers("/actuator/**").permitAll()
                        // ADMIN 전용 경로 (ADMIN 권한 필요)
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                        // USER 경로 (USER 또는 ADMIN 권한 필요)
                        .pathMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // 나머지 모든 경로는 인증 필요
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}

