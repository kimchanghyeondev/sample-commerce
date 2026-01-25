-- product_option_group 테이블에서 product_template_id 컬럼 제거
-- ProductOptionGroup이 이제 Product에 직접 연결되므로 product_template_id는 더 이상 필요하지 않음

USE toy_commerce;

-- 기존 외래키 제약조건 제거 (존재하는 경우)
SET @constraint_name = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'toy_commerce' 
    AND TABLE_NAME = 'product_option_group' 
    AND COLUMN_NAME = 'product_template_id' 
    AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL, 
    CONCAT('ALTER TABLE product_option_group DROP FOREIGN KEY ', @constraint_name, ';'),
    'SELECT "No foreign key constraint found" AS message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- product_template_id 컬럼 제거
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'toy_commerce' 
    AND TABLE_NAME = 'product_option_group' 
    AND COLUMN_NAME = 'product_template_id'
);

SET @sql = IF(@column_exists > 0, 
    'ALTER TABLE product_option_group DROP COLUMN product_template_id;',
    'SELECT "Column product_template_id does not exist" AS message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- product_id 컬럼이 없으면 추가
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'toy_commerce' 
    AND TABLE_NAME = 'product_option_group' 
    AND COLUMN_NAME = 'product_id'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE product_option_group ADD COLUMN product_id BIGINT NOT NULL AFTER id;',
    'SELECT "Column product_id already exists" AS message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- product_id에 외래키 제약조건 추가 (이미 있으면 무시)
SET @constraint_exists = (
    SELECT COUNT(*) 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'toy_commerce' 
    AND TABLE_NAME = 'product_option_group' 
    AND COLUMN_NAME = 'product_id' 
    AND REFERENCED_TABLE_NAME = 'product'
);

SET @sql = IF(@constraint_exists = 0, 
    'ALTER TABLE product_option_group ADD CONSTRAINT fk_product_option_group_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;',
    'SELECT "Foreign key constraint already exists" AS message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

