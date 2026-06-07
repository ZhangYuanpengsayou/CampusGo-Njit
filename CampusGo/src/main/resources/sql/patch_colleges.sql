USE `db-campusgo-njit`;

CREATE TABLE IF NOT EXISTS colleges (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500),
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_colleges_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP INDEX idx_colleges_status ON colleges;
CREATE INDEX idx_colleges_status ON colleges(status);

TRUNCATE TABLE colleges;

INSERT INTO colleges (name, description, status) VALUES
('计算机学院', '计算机相关专业学院', 1),
('外国语学院', '外语相关专业学院', 1),
('商学院', '经济管理相关专业学院', 1),
('艺术学院', '艺术设计相关专业学院', 1);
