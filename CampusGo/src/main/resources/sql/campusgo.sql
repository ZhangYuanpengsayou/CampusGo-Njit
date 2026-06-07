CREATE DATABASE IF NOT EXISTS `db-campusgo-njit`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `db-campusgo-njit`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS repair_applications;
DROP TABLE IF EXISTS leave_applications;
DROP TABLE IF EXISTS carousel_items;
DROP TABLE IF EXISTS dorm_rooms;
DROP TABLE IF EXISTS dorm_buildings;
DROP TABLE IF EXISTS class_groups;
DROP TABLE IF EXISTS majors;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS admin_profiles;
DROP TABLE IF EXISTS teacher_profiles;
DROP TABLE IF EXISTS student_profiles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS colleges;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  phone VARCHAR(20),
  email VARCHAR(100),
  college VARCHAR(100),
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_users_role CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
  CONSTRAINT chk_users_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE colleges (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500),
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_colleges_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE majors (
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

CREATE TABLE class_groups (
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

CREATE TABLE dorm_buildings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_dorm_buildings_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dorm_rooms (
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

CREATE TABLE student_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  student_no VARCHAR(30) NOT NULL UNIQUE,
  major VARCHAR(100),
  class_name VARCHAR(100),
  dorm_building VARCHAR(50),
  dorm_room VARCHAR(50),
  CONSTRAINT fk_student_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE teacher_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  teacher_no VARCHAR(30) NOT NULL UNIQUE,
  title VARCHAR(50),
  office VARCHAR(100),
  CONSTRAINT fk_teacher_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE admin_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  admin_no VARCHAR(30) NOT NULL UNIQUE,
  department VARCHAR(100),
  CONSTRAINT fk_admin_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE announcements (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  publisher_id BIGINT NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_announcements_publisher FOREIGN KEY (publisher_id) REFERENCES users(id),
  CONSTRAINT chk_announcements_deleted CHECK (deleted IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE carousel_items (
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

CREATE TABLE leave_applications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  college VARCHAR(100) NOT NULL,
  reason VARCHAR(500) NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  auditor_id BIGINT,
  audit_opinion VARCHAR(500),
  audit_time DATETIME,
  return_time DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_leave_applications_student FOREIGN KEY (student_id) REFERENCES users(id),
  CONSTRAINT fk_leave_applications_auditor FOREIGN KEY (auditor_id) REFERENCES users(id),
  CONSTRAINT chk_leave_applications_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELED', 'RETURNED')),
  CONSTRAINT chk_leave_applications_time CHECK (start_time <= end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE repair_applications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  reason VARCHAR(500) NOT NULL,
  photo_url VARCHAR(255),
  dorm_building VARCHAR(50),
  dorm_room VARCHAR(50),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  auditor_id BIGINT,
  audit_opinion VARCHAR(500),
  audit_time DATETIME,
  repairman_phone VARCHAR(20),
  score INT,
  comment VARCHAR(500),
  comment_time DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_repair_applications_student FOREIGN KEY (student_id) REFERENCES users(id),
  CONSTRAINT fk_repair_applications_auditor FOREIGN KEY (auditor_id) REFERENCES users(id),
  CONSTRAINT chk_repair_applications_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELED', 'REPAIRING', 'COMPLETED', 'RATED')),
  CONSTRAINT chk_repair_applications_score CHECK (score IS NULL OR score BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_colleges_status ON colleges(status);
CREATE INDEX idx_majors_college_status ON majors(college_id, status);
CREATE INDEX idx_class_groups_major_status ON class_groups(major_id, status);
CREATE INDEX idx_dorm_rooms_building_status ON dorm_rooms(building_id, status);
CREATE INDEX idx_announcements_created_at ON announcements(created_at);
CREATE INDEX idx_carousel_items_status_sort ON carousel_items(status, sort_order);
CREATE INDEX idx_leave_student_status ON leave_applications(student_id, status);
CREATE INDEX idx_leave_college_status ON leave_applications(college, status);
CREATE INDEX idx_repair_student_status ON repair_applications(student_id, status);
CREATE INDEX idx_repair_status_created_at ON repair_applications(status, created_at);

INSERT INTO colleges (name, description, status) VALUES
('计算机学院', '计算机相关专业学院', 1),
('外国语学院', '外语相关专业学院', 1),
('商学院', '经济管理相关专业学院', 1),
('艺术学院', '艺术设计相关专业学院', 1);

INSERT INTO majors (college_id, name, status)
SELECT id, '软件工程', 1 FROM colleges WHERE name = '计算机学院'
UNION ALL SELECT id, '计算机科学与技术', 1 FROM colleges WHERE name = '计算机学院'
UNION ALL SELECT id, '英语', 1 FROM colleges WHERE name = '外国语学院'
UNION ALL SELECT id, '工商管理', 1 FROM colleges WHERE name = '商学院'
UNION ALL SELECT id, '视觉传达设计', 1 FROM colleges WHERE name = '艺术学院';

INSERT INTO class_groups (major_id, name, status)
SELECT id, '软件2401', 1 FROM majors WHERE name = '软件工程'
UNION ALL SELECT id, '计科2401', 1 FROM majors WHERE name = '计算机科学与技术'
UNION ALL SELECT id, '英语2401', 1 FROM majors WHERE name = '英语'
UNION ALL SELECT id, '工商2401', 1 FROM majors WHERE name = '工商管理'
UNION ALL SELECT id, '视传2401', 1 FROM majors WHERE name = '视觉传达设计';

INSERT INTO dorm_buildings (name, status) VALUES
('3栋', 1),
('5栋', 1);

INSERT INTO dorm_rooms (building_id, room_no, status)
SELECT id, '502', 1 FROM dorm_buildings WHERE name = '3栋'
UNION ALL SELECT id, '503', 1 FROM dorm_buildings WHERE name = '3栋'
UNION ALL SELECT id, '608', 1 FROM dorm_buildings WHERE name = '5栋';

INSERT INTO carousel_items (title, subtitle, image_url, sort_order, status) VALUES
('校园事务，一站抵达。', '公告查看、请假申请、公寓报修和事务审核集中处理，让日常校园服务更清晰、更轻快。', '/assets/campus-hero.png', 1, 1);
