# 프로젝트 아키텍처

## 전체 시스템 구조

```
                                 ┌─────────────────────────────────────┐
                                 │            Client                   │
                                 │      (Web / Mobile App)             │
                                 └─────────────────┬───────────────────┘
                                                   │
                                                   ▼
                         ┌─────────────────────────────────────────────────────┐
                         │                 API Gateway                          │
                         │                  :8080                               │
                         │  ┌─────────────────────────────────────────────┐    │
                         │  │  • JWT 인증/검증                             │    │
                         │  │  • 요청 라우팅                               │    │
                         │  │  • CORS 설정                                 │    │
                         │  │  • AuthController (로그인)                   │    │
                         │  └─────────────────────────────────────────────┘    │
                         └───────────────────────┬─────────────────────────────┘
                                                 │
                        ┌────────────────────────┼────────────────────────┐
                        │                        │                        │
                        ▼                        │                        ▼
         ┌──────────────────────────┐           │         ┌──────────────────────────┐
         │      User Service        │           │         │     Admin Service        │
         │         :8081            │           │         │        :8082             │
         │  ┌────────────────────┐  │           │         │  ┌────────────────────┐  │
         │  │ • UserController   │  │           │         │  │ • AdminController  │  │
         │  │ • SecurityConfig   │  │           │         │  │                    │  │
         │  │ • JwtFilter        │  │           │         │  │                    │  │
         │  └────────────────────┘  │           │         │  └────────────────────┘  │
         └────────────┬─────────────┘           │         └────────────┬─────────────┘
                      │                         │                      │
                      ▼                         │                      ▼
              ┌──────────────┐                  │              ┌──────────────┐
              │    MySQL     │                  │              │   H2 (Dev)   │
              └──────────────┘                  │              └──────────────┘
                                                │
                                                ▼
                         ┌─────────────────────────────────────────────────────┐
                         │                 Common Module                        │
                         │  ┌─────────────────────────────────────────────┐    │
                         │  │  Entity    │  Security  │  Config           │    │
                         │  │  ─────────────────────────────────────────  │    │
                         │  │  • User       • JwtUtil    • PasswordEncoder│    │
                         │  │  • Product    • RequireAuth                 │    │
                         │  │  • Category   • RequireRole                 │    │
                         │  └─────────────────────────────────────────────┘    │
                         └─────────────────────────────────────────────────────┘
```

---

## 모듈 구성

| 모듈 | 포트 | 역할 | 데이터베이스 |
|------|------|------|-------------|
| **gateway** | 8080 | API 게이트웨이, 라우팅 및 인증 | MySQL |
| **user** | 8081 | 사용자 서비스 | MySQL |
| **admin** | 8082 | 관리자 서비스 | H2 (인메모리) |
| **common** | - | 공유 라이브러리 | - |

---

## 라우팅 흐름

```
  Client Request
       │
       ▼
  ┌─────────────────────────────────────────────────────────┐
  │                    Gateway :8080                         │
  │                                                          │
  │   /api/user/**  ─────────────────►  User Service :8081  │
  │                                                          │
  │   /api/admin/** ─────────────────►  Admin Service :8082 │
  │                                                          │
  │   /auth/login   ─────────────────►  AuthController      │
  └─────────────────────────────────────────────────────────┘
```

---

## 모듈 의존성

```
                    ┌─────────────┐
                    │   common    │
                    │  (공통 모듈)  │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │ gateway  │    │   user   │    │  admin   │
    │  :8080   │    │  :8081   │    │  :8082   │
    └──────────┘    └──────────┘    └──────────┘
```

---

## 인증 흐름

```
  ┌────────┐         ┌──────────┐         ┌─────────────┐
  │ Client │         │ Gateway  │         │ User/Admin  │
  └───┬────┘         └────┬─────┘         └──────┬──────┘
      │                   │                      │
      │  1. POST /auth/login                     │
      │  (email, password)│                      │
      │──────────────────►│                      │
      │                   │                      │
      │  2. JWT Token     │                      │
      │◄──────────────────│                      │
      │                   │                      │
      │  3. Request + JWT │                      │
      │──────────────────►│                      │
      │                   │  4. 검증 후 라우팅     │
      │                   │─────────────────────►│
      │                   │                      │
      │                   │  5. Response         │
      │                   │◄─────────────────────│
      │  6. Response      │                      │
      │◄──────────────────│                      │
      │                   │                      │
```

---

## Entity 관계도

```
  ┌─────────────────┐
  │   BaseEntity    │
  │  ─────────────  │
  │  • id           │
  │  • createdAt    │
  │  • updatedAt    │
  └────────┬────────┘
           │ extends
     ┌─────┴─────┬─────────────┬─────────────────┐
     ▼           ▼             ▼                 ▼
┌─────────┐ ┌─────────┐ ┌───────────────┐ ┌──────────┐
│  User   │ │ Product │ │ProductTemplate│ │ Category │
│─────────│ │─────────│ │───────────────│ │──────────│
│• email  │ │• name   │ │• name         │ │• name    │
│• passwd │ │• price  │ │• description  │ │• depth   │
│• role   │ │• status │ │               │ │• parent  │
└─────────┘ └─────────┘ └───────────────┘ └──────────┘
                │                │              │
                ▼                │              │
     ┌───────────────────┐       │              │
     │ ProductOptionGroup│◄──────┘              │
     │───────────────────│                      │
     │ • name            │                      │
     └─────────┬─────────┘                      │
               │                                │
               ▼                                ▼
     ┌───────────────────┐    ┌─────────────────────────────────┐
     │  ProductOption    │    │CategoryProductTemplateMapping   │
     │───────────────────│    │─────────────────────────────────│
     │ • name            │    │ • category                      │
     │ • additionalPrice │    │ • productTemplate               │
     └───────────────────┘    └─────────────────────────────────┘
```

---

## 디렉토리 구조

```
sample-commerce/
├── gateway/                # API 게이트웨이 모듈
│   ├── src/main/java/com/toycommerce/gateway/
│   │   ├── GatewayApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   ├── filter/
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   └── UserService.java
│   │   └── repository/
│   │       └── UserRepository.java
│   └── src/main/resources/application.yml
│
├── user/                   # 사용자 서비스 모듈
│   ├── src/main/java/com/toycommerce/user/
│   │   ├── UserApplication.java
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/UserController.java
│   │   └── filter/JwtAuthenticationFilter.java
│   └── src/main/resources/application.yml
│
├── admin/                  # 관리자 서비스 모듈
│   ├── src/main/java/com/toycommerce/admin/
│   │   ├── AdminApplication.java
│   │   └── controller/AdminController.java
│   └── src/main/resources/application.yml
│
├── common/                 # 공통 라이브러리 모듈
│   └── src/main/java/com/toycommerce/common/
│       ├── annotation/
│       │   ├── RequireAuth.java
│       │   └── RequireRole.java
│       ├── config/
│       │   └── PasswordEncoderConfig.java
│       ├── entity/
│       │   ├── BaseEntity.java
│       │   ├── user/
│       │   │   ├── User.java
│       │   │   └── Role.java
│       │   ├── product/
│       │   │   ├── Product.java
│       │   │   ├── ProductTemplate.java
│       │   │   ├── ProductOption.java
│       │   │   └── ProductOptionGroup.java
│       │   ├── category/
│       │   │   ├── Category.java
│       │   │   └── CategoryProductTemplateMapping.java
│       │   └── enums/
│       │       └── EntityStatus.java
│       └── util/
│           └── JwtUtil.java
│
├── database/               # 데이터베이스 관련
├── build.gradle            # 루트 빌드 설정
├── settings.gradle         # 멀티모듈 설정
└── gradle/wrapper/         # Gradle Wrapper
```

---

## 기술 스택

| 항목 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Cloud | Spring Cloud 2023.0.0 |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Security | Spring Security + JWT (JJWT 0.12.3) |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL (prod) / H2 (dev) |
| Build | Gradle 8.5 |
| Utility | Lombok |
