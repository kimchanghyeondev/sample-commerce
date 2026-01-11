-- 간단한 방법: 기존 users 테이블의 데이터를 업데이트
-- 이 스크립트를 먼저 실행하세요

USE toy_commerce;

-- 컬럼이 이미 존재하는지 확인하고, 없으면 nullable로 추가
-- (수동으로 실행하거나 아래 스크립트 사용)

-- 방법 1: 컬럼이 없을 때만 추가 (에러 무시)
-- ALTER TABLE users ADD COLUMN created_at DATETIME(6) NULL;
-- ALTER TABLE users ADD COLUMN updated_at DATETIME(6) NULL;
-- ALTER TABLE users ADD COLUMN created_by VARCHAR(100) NULL;
-- ALTER TABLE users ADD COLUMN updated_by VARCHAR(100) NULL;

-- 방법 2: 기존 데이터 업데이트 (컬럼이 이미 추가된 경우)
UPDATE users 
SET created_at = COALESCE(created_at, NOW(6)), 
    updated_at = COALESCE(updated_at, NOW(6))
WHERE created_at IS NULL OR updated_at IS NULL OR created_at = '0000-00-00 00:00:00' OR updated_at = '0000-00-00 00:00:00';

-- 방법 3: 잘못된 날짜 값 수정
UPDATE users 
SET created_at = NOW(6)
WHERE created_at = '0000-00-00 00:00:00' OR created_at IS NULL;

UPDATE users 
SET updated_at = NOW(6)
WHERE updated_at = '0000-00-00 00:00:00' OR updated_at IS NULL;

-- 이제 NOT NULL 제약조건 추가
ALTER TABLE users 
MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE users 
MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

