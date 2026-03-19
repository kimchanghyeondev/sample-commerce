package com.toycommerce.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String API_DESCRIPTION = """
            프리미엄 수산물 직송 플랫폼 Fresh Blue의 사용자 서비스 API 문서입니다.
            
            ## 🔐 테스트 계정 정보
            
            | 역할 | 아이디 | 비밀번호 |
            |------|--------|----------|
            | 관리자 | `admin` | `admin` |
            | 일반 사용자 | `user` | `user` |
            
            ## 🔑 인증 방법
            
            1. Gateway 서비스(http://localhost:8080)의 `/api/auth/login` API로 로그인
            2. 응답받은 JWT 토큰을 복사
            3. 우측 상단 **Authorize** 버튼 클릭
            4. 토큰 입력 후 **Authorize** 클릭
            """;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Authentication";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Fresh Blue - User Service API")
                        .description(API_DESCRIPTION)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Fresh Blue Team")
                                .email("support@freshblue.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development Server"),
                        new Server().url("http://localhost:8080").description("Gateway Server (로그인용)")))
                // 전역 인증 요구사항 제거 - 개별 API에서 @SecurityRequirement로 지정
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요. (Bearer 접두사 불필요)\n\n" +
                                                "**테스트 계정:** admin / admin 또는 user / user")));
    }
}

