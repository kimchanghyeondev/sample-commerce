# 리팩터링 제안서

## 1. 코드 중복 (Code Duplication)

### 1.1 JWT 토큰 추출 코드 중복

**위치**:
- `user/src/main/java/com/toycommerce/user/filter/JwtAuthenticationFilter.java:131-136`
- `gateway/src/main/java/com/toycommerce/gateway/filter/JwtAuthenticationFilter.java:74-80`

**현재 코드**:
```java
private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}
```

**개선안**: `common` 모듈에 `TokenExtractor` 유틸 클래스 생성
```java
// common/src/main/java/com/toycommerce/common/util/TokenExtractor.java
public class TokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String extract(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
```

---

### 1.2 역할 검증 로직 중복

**위치**: `user/filter/JwtAuthenticationFilter.java:99-117`

**개선안**: `RoleValidator` 유틸 클래스로 분리
```java
// common/src/main/java/com/toycommerce/common/security/RoleValidator.java
public class RoleValidator {
    public static boolean hasRequiredRole(List<String> userRoles, Role[] requiredRoles) {
        return userRoles.stream()
            .anyMatch(role -> Arrays.stream(requiredRoles)
                .anyMatch(required -> role.equals(required.name())));
    }
}
```

---

### 1.3 잘못된 로그 메시지

**위치**: `gateway/service/AuthService.java:25,31`

**현재 코드**:
```java
log.error("Category not found: {}", username);  // 잘못된 메시지
log.error("Category is disabled: {}", username); // 잘못된 메시지
```

**개선안**:
```java
log.error("User not found: {}", username);
log.error("User is disabled: {}", username);
```

---

## 2. 보안 이슈 (Security)

### 2.1 JWT 시크릿 키 노출 (심각도: 높음)

**위치**: 여러 `application.yml` 파일

**현재 코드**:
```yaml
jwt:
  secret: your-256-bit-secret-key-for-hmac-sha256-algorithm-minimum-32-characters
  expiration: 86400000
```

**개선안**:
```yaml
jwt:
  secret: ${JWT_SECRET:default-dev-secret-key-minimum-32-characters}
  expiration: ${JWT_EXPIRATION:86400000}
```

---

### 2.2 데이터베이스 연결 보안 (심각도: 높음)

**위치**: `application.yml` 여러 파일

**현재 코드**:
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/toy_commerce?allowPublicKeyRetrieval=true
  username: root
  password: root
```

**개선안**:
```yaml
datasource:
  url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:toy_commerce}?useSSL=true&serverTimezone=Asia/Seoul
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}
```

---

### 2.3 CORS 설정 과도한 허용

**위치**: `gateway/config/CorsConfig.java:21`

**현재 코드**:
```java
corsConfig.setAllowedHeaders(Arrays.asList("*"));
```

**개선안**:
```java
corsConfig.setAllowedHeaders(Arrays.asList(
    "Content-Type",
    "Authorization",
    "X-Requested-With"
));
```

---

### 2.4 토큰 검증 예외 처리 미흡

**위치**: `common/util/JwtUtil.java:67-74`

**현재 코드**:
```java
public Boolean validateToken(String token) {
    try {
        return !isTokenExpired(token);
    } catch (Exception e) {
        log.error("JWT 토큰 검증 실패", e);
        return false;
    }
}
```

**개선안**:
```java
public void validateToken(String token) throws InvalidTokenException {
    try {
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token);
    } catch (SignatureException e) {
        throw new InvalidTokenException("Invalid token signature", e);
    } catch (ExpiredJwtException e) {
        throw new ExpiredTokenException("Token has expired", e);
    } catch (MalformedJwtException e) {
        throw new InvalidTokenException("Malformed token", e);
    }
}
```

---

### 2.5 비밀번호 인코딩 강도 설정 부재

**위치**: `common/config/PasswordEncoderConfig.java`

**현재 코드**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**개선안**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // strength 명시
}
```

---

## 3. 예외 처리 (Exception Handling)

### 3.1 전역 예외 핸들러 부재

**현재 상태**: 예외 처리가 각 필터와 컨트롤러에 분산

**개선안**:

```java
// common/src/main/java/com/toycommerce/common/exception/ErrorCode.java
public enum ErrorCode {
    INVALID_CREDENTIALS("AUTH001", "Invalid credentials"),
    UNAUTHORIZED("AUTH002", "Unauthorized"),
    FORBIDDEN("AUTH003", "Forbidden"),
    TOKEN_EXPIRED("AUTH004", "Token has expired"),
    INVALID_TOKEN("AUTH005", "Invalid token"),
    USER_NOT_FOUND("USER001", "User not found"),
    INTERNAL_ERROR("SYS001", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
```

```java
// common/src/main/java/com/toycommerce/common/exception/BusinessException.java
public abstract class BusinessException extends RuntimeException {
    public abstract ErrorCode getErrorCode();

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
// common/src/main/java/com/toycommerce/common/exception/UnauthorizedException.java
public class UnauthorizedException extends BusinessException {
    private final ErrorCode errorCode;

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED.getMessage());
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

```java
// common/src/main/java/com/toycommerce/common/dto/ErrorResponse.java
@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
```

```java
// gateway 또는 각 서비스 모듈
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getErrorCode().getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getErrorCode().getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);
        ErrorResponse response = ErrorResponse.builder()
            .code(ErrorCode.INTERNAL_ERROR.getCode())
            .message(ErrorCode.INTERNAL_ERROR.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

## 4. 설정 관리 (Configuration)

### 4.1 환경별 설정 분리

**현재 상태**: 모든 환경이 같은 설정 파일 사용

**개선안**:

```yaml
# application.yml (공통)
spring:
  application:
    name: ${SERVICE_NAME}
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}
```

```yaml
# application-local.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.toycommerce: DEBUG
    org.hibernate.SQL: DEBUG
```

```yaml
# application-dev.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    com.toycommerce: DEBUG
```

```yaml
# application-prod.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false

logging:
  level:
    root: WARN
    com.toycommerce: INFO
```

---

### 4.2 설정값 검증 추가

**현재 상태**: 시크릿 키 길이, 만료 시간 유효성 검증 없음

**개선안**:
```java
// common/src/main/java/com/toycommerce/common/config/JwtProperties.java
@Component
@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
@Setter
public class JwtProperties {

    @NotBlank(message = "JWT secret is required")
    @Size(min = 32, message = "JWT secret must be at least 32 characters")
    private String secret;

    @NotNull
    @Min(value = 3600000, message = "JWT expiration must be at least 1 hour")
    private Long expiration;
}
```

---

## 5. 코드 품질 (Code Quality)

### 5.1 매직 넘버 제거

**위치**: `user/filter/JwtAuthenticationFilter.java:134`

**현재 코드**:
```java
return bearerToken.substring(7);
```

**개선안**:
```java
private static final String BEARER_PREFIX = "Bearer ";

return bearerToken.substring(BEARER_PREFIX.length());
```

---

### 5.2 하드코딩된 경로 분리

**위치**: `gateway/filter/JwtAuthenticationFilter.java:35-39`

**현재 코드**:
```java
if (path.startsWith("/api/auth") ||
    path.startsWith("/api/categories") ||
    path.startsWith("/api/category-product-templates") ||
    path.startsWith("/api/product-templates") ||
    path.startsWith("/actuator")) {
```

**개선안**:
```java
// common/src/main/java/com/toycommerce/common/config/SecurityPathConfig.java
@Configuration
public class SecurityPathConfig {

    public static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/**",
        "/api/categories/**",
        "/api/category-product-templates/**",
        "/api/product-templates/**",
        "/actuator/**"
    );

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
```

---

### 5.3 Null 안전성 - DTO 도입

**위치**: `gateway/service/AuthService.java:28`

**현재 코드**:
```java
String username = loginRequest.get("username");
String password = loginRequest.get("password");
```

**개선안**:
```java
// common/src/main/java/com/toycommerce/common/dto/LoginRequest.java
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Email(message = "Invalid email format")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

```java
// common/src/main/java/com/toycommerce/common/dto/LoginResponse.java
@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
```

```java
// AuthController.java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    // ...
}
```

---

### 5.4 Setter 남용 제거

**위치**: `common/entity/BaseEntity.java`, `common/entity/category/Category.java`

**현재 코드**:
```java
@Getter
@Setter  // 모든 필드에 Setter 생성
public abstract class BaseEntity {
    // ...
}
```

**개선안**:
```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Setter 제거 - Audit 필드는 자동 관리
}
```

---

### 5.5 타입 캐스팅 안전성

**위치**: `common/util/JwtUtil.java:40-42`

**현재 코드**:
```java
@SuppressWarnings("unchecked")
public List<String> getRolesFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);
    return (List<String>) claims.get("roles");
}
```

**개선안**:
```java
public List<String> getRolesFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);
    Object rolesObj = claims.get("roles");

    if (rolesObj == null) {
        return Collections.emptyList();
    }

    if (rolesObj instanceof List<?> rolesList) {
        return rolesList.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .collect(Collectors.toList());
    }

    throw new InvalidTokenException("Invalid roles format in token");
}
```

---

## 6. 패키지 구조 개선

### 현재 구조
```
gateway/src/main/java/com/toycommerce/gateway/
├── controller/
├── service/
├── filter/
├── config/
├── dto/         (비어있음)
├── repository/
├── router/      (비어있음)
└── GatewayApplication.java
```

### 개선안 (레이어드 아키텍처)
```
gateway/src/main/java/com/toycommerce/gateway/
├── application/
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── request/
│   │   │   └── LoginRequest.java
│   │   └── response/
│   │       └── LoginResponse.java
│   └── GatewayApplication.java
├── domain/
│   └── service/
│       ├── AuthService.java
│       └── UserService.java
├── infrastructure/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   └── repository/
│       └── UserRepository.java
└── common/
    └── exception/
        └── handler/
            └── GlobalExceptionHandler.java
```

### Common 모듈 구조 개선
```
common/src/main/java/com/toycommerce/common/
├── entity/
│   ├── BaseEntity.java
│   ├── user/
│   ├── product/
│   └── category/
├── dto/
│   ├── ErrorResponse.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── exception/
│   ├── ErrorCode.java
│   ├── BusinessException.java
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── InvalidTokenException.java
├── security/
│   ├── TokenExtractor.java
│   ├── RoleValidator.java
│   └── JwtUtil.java
├── config/
│   ├── JwtProperties.java
│   ├── PasswordEncoderConfig.java
│   └── SecurityPathConfig.java
├── annotation/
│   ├── RequireAuth.java
│   └── RequireRole.java
└── util/
```

---

## 7. 엔티티 설계 개선

### 7.1 Product 엔티티

**현재 상태**: 불완전한 필드, 비즈니스 로직 부재

**개선안**:
```java
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id", nullable = false)
    private ProductTemplate productTemplate;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status = EntityStatus.ACTIVE;

    @Builder
    public Product(ProductTemplate productTemplate, String sku, Long price, Integer stock) {
        this.productTemplate = productTemplate;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
    }

    // 비즈니스 로직
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void activate() {
        this.status = EntityStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = EntityStatus.INACTIVE;
    }
}
```

---

## 8. 테스트 코드 추가

### 8.1 JwtUtil 테스트

```java
// common/src/test/java/com/toycommerce/common/util/JwtUtilTest.java
@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 토큰_생성_성공() {
        // given
        String username = "test@example.com";
        List<String> roles = List.of("USER");

        // when
        String token = jwtUtil.generateToken(username, roles);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo(username);
    }

    @Test
    void 만료된_토큰_검증_실패() {
        // given
        String expiredToken = "...";

        // when & then
        assertThat(jwtUtil.validateToken(expiredToken)).isFalse();
    }
}
```

### 8.2 테스트 설정

```yaml
# src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: test-secret-key-for-unit-testing-minimum-32-chars
  expiration: 3600000
```

---

## 9. 우선순위별 실행 로드맵

### Phase 1: 필수/보안 (즉시)
- [ ] JWT 시크릿 환경변수화
- [ ] DB 자격증명 환경변수화
- [ ] 로그 메시지 오류 수정 (`AuthService.java`)
- [ ] 전역 예외 핸들러 구현

### Phase 2: 중요 (1주일)
- [ ] 공통 유틸 분리 (`TokenExtractor`, `RoleValidator`)
- [ ] 환경별 설정 파일 분리
- [ ] DTO 클래스 도입 (`LoginRequest`, `LoginResponse`, `ErrorResponse`)
- [ ] 설정값 검증 (`JwtProperties`)

### Phase 3: 개선 (2주일)
- [ ] 패키지 구조 재구성
- [ ] 엔티티 설계 개선 (Setter 제거, 비즈니스 로직 추가)
- [ ] 커스텀 예외 클래스 정의
- [ ] 매직 넘버/하드코딩 제거

### Phase 4: 품질 (3주일)
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] API 문서화 (Swagger/OpenAPI)
- [ ] 코드 리뷰 및 정리

---

## 10. 체크리스트

### 보안
- [ ] 민감 정보 환경변수 처리
- [ ] CORS 설정 최소화
- [ ] 토큰 검증 강화
- [ ] 비밀번호 인코딩 강도 설정

### 코드 품질
- [ ] 코드 중복 제거
- [ ] 매직 넘버 제거
- [ ] Null 안전성 확보
- [ ] 불필요한 Setter 제거

### 아키텍처
- [ ] 레이어드 아키텍처 적용
- [ ] 전역 예외 처리
- [ ] DTO 패턴 적용
- [ ] 설정 클래스 분리

### 테스트
- [ ] 단위 테스트 커버리지 80% 이상
- [ ] 통합 테스트 주요 플로우
- [ ] 테스트 환경 분리
