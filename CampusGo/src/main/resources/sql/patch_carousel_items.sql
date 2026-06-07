USE `db-campusgo-njit`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS carousel_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  subtitle VARCHAR(300),
  image_url VARCHAR(255) NOT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_carousel_items_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @carousel_index_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'carousel_items'
    AND index_name = 'idx_carousel_items_status_sort'
);
SET @carousel_index_sql = IF(
  @carousel_index_exists = 0,
  'CREATE INDEX idx_carousel_items_status_sort ON carousel_items(status, sort_order)',
  'SELECT 1'
);
PREPARE carousel_index_stmt FROM @carousel_index_sql;
EXECUTE carousel_index_stmt;
DEALLOCATE PREPARE carousel_index_stmt;

INSERT INTO carousel_items (title, subtitle, image_url, sort_order, status)
SELECT '校园事务，一站抵达。',
       '公告查看、请假申请、公寓报修和事务审核集中处理，让日常校园服务更清晰、更轻快。',
       '/assets/campus-hero.png',
       1,
       1
WHERE NOT EXISTS (SELECT 1 FROM carousel_items);
