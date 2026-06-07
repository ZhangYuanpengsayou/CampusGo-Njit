USE `db-campusgo-njit`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS majors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  college_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_majors_college FOREIGN KEY (college_id) REFERENCES colleges(id),
  CONSTRAINT uk_majors_college_name UNIQUE (college_id, name),
  CONSTRAINT chk_majors_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS class_groups (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  major_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_class_groups_major FOREIGN KEY (major_id) REFERENCES majors(id),
  CONSTRAINT uk_class_groups_major_name UNIQUE (major_id, name),
  CONSTRAINT chk_class_groups_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS dorm_buildings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_dorm_buildings_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS dorm_rooms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  building_id BIGINT NOT NULL,
  room_no VARCHAR(50) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_dorm_rooms_building FOREIGN KEY (building_id) REFERENCES dorm_buildings(id),
  CONSTRAINT uk_dorm_rooms_building_room UNIQUE (building_id, room_no),
  CONSTRAINT chk_dorm_rooms_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO majors (college_id, name, status)
SELECT id, '软件工程', 1 FROM colleges WHERE name = '计算机学院'
UNION ALL SELECT id, '计算机科学与技术', 1 FROM colleges WHERE name = '计算机学院'
UNION ALL SELECT id, '英语', 1 FROM colleges WHERE name = '外国语学院'
UNION ALL SELECT id, '工商管理', 1 FROM colleges WHERE name = '商学院'
UNION ALL SELECT id, '视觉传达设计', 1 FROM colleges WHERE name = '艺术学院';

INSERT IGNORE INTO class_groups (major_id, name, status)
SELECT id, '软件2401', 1 FROM majors WHERE name = '软件工程'
UNION ALL SELECT id, '计科2401', 1 FROM majors WHERE name = '计算机科学与技术'
UNION ALL SELECT id, '英语2401', 1 FROM majors WHERE name = '英语'
UNION ALL SELECT id, '工商2401', 1 FROM majors WHERE name = '工商管理'
UNION ALL SELECT id, '视传2401', 1 FROM majors WHERE name = '视觉传达设计';

INSERT IGNORE INTO dorm_buildings (name, status) VALUES
('3栋', 1),
('5栋', 1);

INSERT IGNORE INTO dorm_rooms (building_id, room_no, status)
SELECT id, '502', 1 FROM dorm_buildings WHERE name = '3栋'
UNION ALL SELECT id, '503', 1 FROM dorm_buildings WHERE name = '3栋'
UNION ALL SELECT id, '608', 1 FROM dorm_buildings WHERE name = '5栋';
