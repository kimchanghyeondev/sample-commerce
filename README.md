# sample-commerce

## 로컬 DB (MySQL) 도커 실행

아래 명령은 이미지 풀부터 컨테이너 실행까지 한번에 처리합니다. 데이터는 프로젝트 내 `.mysql-data/`에 영구 저장됩니다.

```bash
docker pull mysql:8.0
docker run -d --name sample-commerce-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=toy_commerce \
  -v "$(pwd)/.mysql-data:/var/lib/mysql" \
  -v "$(pwd)/database:/docker-entrypoint-initdb.d:ro" \
  mysql:8.0
```

상태 확인:

```bash
docker ps --filter name=sample-commerce-mysql
```

중지/삭제:

```bash
docker stop sample-commerce-mysql
docker rm sample-commerce-mysql
```
