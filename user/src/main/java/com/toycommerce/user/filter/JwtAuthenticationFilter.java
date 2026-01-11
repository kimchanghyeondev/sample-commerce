package com.toycommerce.user.filter;

import com.toycommerce.common.annotation.RequireAuth;
import com.toycommerce.common.annotation.RequireRole;
import com.toycommerce.common.entity.user.Role;
import com.toycommerce.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 핸들러 메서드 찾기
        HandlerMethod handlerMethod = getHandlerMethod(request);

        if (handlerMethod == null) {
            // 핸들러가 없으면 다음 필터로 진행
            filterChain.doFilter(request, response);
            return;
        }

        // @RequireAuth 또는 @RequireRole 어노테이션 확인
        RequireAuth requireAuth = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireAuth.class);
        RequireRole requireRole = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireRole.class);

        // 클래스 레벨 어노테이션도 확인
        if (requireAuth == null) {
            requireAuth = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireAuth.class);
        }
        if (requireRole == null) {
            requireRole = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireRole.class);
        }

        // 인증/인가가 필요하지 않으면 다음 필터로 진행 (토큰이 있어도 검증하지 않음)
        if (requireAuth == null && requireRole == null) {
            // 토큰이 있으면 SecurityContext에 설정 (선택적)
            String token = extractToken(request);
            if (token != null && jwtUtil.validateToken(token)) {
                try {
                    String username = jwtUtil.getUsernameFromToken(token);
                    List<String> roles = jwtUtil.getRolesFromToken(token);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.debug("토큰 파싱 실패 (무시): {}", e.getMessage());
                }
            }
            filterChain.doFilter(request, response);
            return;
        }

        // 어노테이션이 있는 경우에만 JWT 토큰 검증 필수
        String token = extractToken(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"인증이 필요합니다.\"}");
            return;
        }

        // 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUsernameFromToken(token);
        List<String> roles = jwtUtil.getRolesFromToken(token);

        // 역할 검증 (@RequireRole이 있는 경우)
        if (requireRole != null) {
            Role[] requiredRoles = requireRole.value();
            boolean hasRequiredRole = roles.stream()
                    .anyMatch(role -> {
                        for (Role requiredRoleEnum : requiredRoles) {
                            if (role.equals(requiredRoleEnum.name())) {
                                return true;
                            }
                        }
                        return false;
                    });

            if (!hasRequiredRole) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"권한이 없습니다.\"}");
                return;
            }
        }

        // SecurityContext에 인증 정보 설정
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null && handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                return (HandlerMethod) handlerExecutionChain.getHandler();
            }
        } catch (Exception e) {
            log.debug("핸들러 메서드를 찾을 수 없습니다: {}", e.getMessage());
        }
        return null;
    }
}

