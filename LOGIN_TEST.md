# 로그인 테스트 가이드

## 기본 로그인 테스트

### 1. Admin 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

**예상 응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. User 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user"}'
```

**예상 응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. 잘못된 자격증명 테스트
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"wrong","password":"wrong"}'
```

**예상 응답:** `401 Unauthorized`

## 토큰 검증 테스트

### 토큰 검증
```bash
# TOKEN 변수에 토큰 저장
TOKEN="발급받은토큰"

curl -X POST http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer ${TOKEN}"
```

**예상 응답:**
```json
{
  "valid": true
}
```

## 인증이 필요한 API 테스트

### 1. User API 접근 (USER 또는 ADMIN 권한)
```bash
TOKEN="발급받은토큰"

curl -X GET http://localhost:8080/api/user/hello \
  -H "Authorization: Bearer ${TOKEN}"
```

### 2. Admin API 접근 (ADMIN 권한만)
```bash
TOKEN="발급받은토큰"

curl -X GET http://localhost:8080/api/admin/hello \
  -H "Authorization: Bearer ${TOKEN}"
```

**USER 권한으로 접근 시:** `403 Forbidden`

## 전체 테스트 스크립트 실행

```bash
chmod +x test-login.sh
./test-login.sh
```

## 초기 사용자 계정

- **Admin**: username=`admin`, password=`admin` (ADMIN 권한)
- **User**: username=`user`, password=`user` (USER 권한)

## 주의사항

1. Gateway 서버가 실행 중이어야 합니다 (포트 8080)
2. MySQL 데이터베이스가 실행 중이어야 합니다
3. `toy_commerce` 데이터베이스가 생성되어 있어야 합니다
4. 초기 사용자는 애플리케이션 시작 시 자동으로 생성됩니다

