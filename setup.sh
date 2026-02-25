#!/bin/bash
set -e

echo "========================================="
echo " Sample Commerce - Ubuntu Server Setup"
echo "========================================="

# Docker 설치 여부 확인
if ! command -v docker &> /dev/null; then
    echo "[1/5] Docker 설치 중..."
    sudo apt-get update
    sudo apt-get install -y ca-certificates curl
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc

    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
      $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
      sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

    sudo usermod -aG docker $USER
    echo "Docker 설치 완료. docker 그룹 적용을 위해 재로그인이 필요할 수 있습니다."
else
    echo "[1/5] Docker 이미 설치됨 - 건너뜀"
fi

# Docker 서비스 실행 확인
if ! sudo systemctl is-active --quiet docker; then
    echo "[2/5] Docker 서비스 시작..."
    sudo systemctl start docker
    sudo systemctl enable docker
else
    echo "[2/5] Docker 서비스 실행 중 - 건너뜀"
fi

# JDK 17 설치 여부 확인
if ! command -v java &> /dev/null; then
    echo "[3/5] JDK 17 설치 중..."
    sudo apt-get install -y openjdk-17-jdk
else
    echo "[3/5] JDK 이미 설치됨 - 건너뜀"
fi

# Gradle 빌드 (1회)
echo "[4/5] Gradle 빌드 중..."
chmod +x ./gradlew 2>/dev/null || true
if [ -f "./gradlew" ]; then
    ./gradlew clean build -x test --no-daemon
else
    gradle clean build -x test --no-daemon
fi

# Docker Compose 실행
echo "[5/5] 기존 컨테이너 정리 후 실행..."
docker compose down 2>/dev/null || true
docker compose up --build -d

echo ""
echo "========================================="
echo " 실행 완료"
echo "========================================="
echo " Gateway  : http://localhost:8000"
echo " User     : http://localhost:8081"
echo " Admin    : http://localhost:8082"
echo " MySQL    : localhost:3306"
echo ""
echo " Health Check:"
echo "   curl http://localhost:8000/actuator/health"
echo "   curl http://localhost:8081/actuator/health"
echo "   curl http://localhost:8082/actuator/health"
echo ""
echo " 로그 확인: docker compose logs -f"
echo " 중지:     docker compose down"
echo "========================================="
