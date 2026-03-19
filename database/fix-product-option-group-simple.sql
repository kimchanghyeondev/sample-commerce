-- product_option_group 테이블 수정 (간단 버전)
-- ProductOptionGroup이 이제 Product에 직접 연결되므로 product_template_id는 더 이상 필요하지 않음

USE toy_commerce;

-- 1. 기존 외래키 제약조건 제거 (수동으로 확인 후 실행)
-- ALTER TABLE product_option_group DROP FOREIGN KEY fk_product_option_group_product_template;

-- 2. product_template_id 컬럼 제거
ALTER TABLE product_option_group DROP COLUMN product_template_id;

-- 3. product_id 컬럼 추가 (이미 있으면 에러 발생하므로 확인 후 실행)
-- ALTER TABLE product_option_group ADD COLUMN product_id BIGINT NOT NULL AFTER id;

-- 4. product_id에 외래키 제약조건 추가
-- ALTER TABLE product_option_group ADD CONSTRAINT fk_product_option_group_product 
-- FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;

