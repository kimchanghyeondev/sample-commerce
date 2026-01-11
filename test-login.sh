#!/bin/bash

# Gateway 서버 주소
GATEWAY_URL="http://localhost:8080"

echo "=== 로그인 테스트 ==="
echo ""

# 1. Admin 로그인
echo "1. Admin 로그인 (admin/admin):"
echo "----------------------------------------"
ADMIN_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}')

echo "$ADMIN_RESPONSE" | jq '.' 2>/dev/null || echo "$ADMIN_RESPONSE"
echo ""

# 토큰 추출 (jq가 있는 경우)
ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ "$ADMIN_TOKEN" != "null" ] && [ -n "$ADMIN_TOKEN" ]; then
  echo "Admin Token: ${ADMIN_TOKEN:0:50}..."
  echo ""
  
  # 토큰 검증
  echo "2. Admin 토큰 검증:"
  echo "----------------------------------------"
  curl -s -X POST "${GATEWAY_URL}/api/auth/validate" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" | jq '.' 2>/dev/null || echo "Token validation response"
  echo ""
  
  # Admin 전용 API 테스트
  echo "3. Admin 전용 API 테스트 (/api/admin/hello):"
  echo "----------------------------------------"
  curl -s -X GET "${GATEWAY_URL}/api/admin/hello" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" | jq '.' 2>/dev/null || echo "Response"
  echo ""
fi

# 2. User 로그인
echo "4. User 로그인 (user/user):"
echo "----------------------------------------"
USER_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user"}')

echo "$USER_RESPONSE" | jq '.' 2>/dev/null || echo "$USER_RESPONSE"
echo ""

# 토큰 추출
USER_TOKEN=$(echo "$USER_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ "$USER_TOKEN" != "null" ] && [ -n "$USER_TOKEN" ]; then
  echo "User Token: ${USER_TOKEN:0:50}..."
  echo ""
  
  # User API 테스트
  echo "5. User API 테스트 (/api/user/hello):"
  echo "----------------------------------------"
  curl -s -X GET "${GATEWAY_URL}/api/user/hello" \
    -H "Authorization: Bearer ${USER_TOKEN}" | jq '.' 2>/dev/null || echo "Response"
  echo ""
  
  # Admin 전용 API 접근 시도 (실패해야 함)
  echo "6. User가 Admin API 접근 시도 (403 Forbidden 예상):"
  echo "----------------------------------------"
  curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${GATEWAY_URL}/api/admin/hello" \
    -H "Authorization: Bearer ${USER_TOKEN}" | jq '.' 2>/dev/null || echo "Response"
  echo ""
fi

# 3. 잘못된 자격증명 테스트
echo "7. 잘못된 자격증명 테스트 (401 Unauthorized 예상):"
echo "----------------------------------------"
curl -s -w "\nHTTP Status: %{http_code}\n" -X POST "${GATEWAY_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"wrong","password":"wrong"}' | jq '.' 2>/dev/null || echo "Response"
echo ""

echo "=== 테스트 완료 ==="

