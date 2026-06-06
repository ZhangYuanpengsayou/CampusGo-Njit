# 高校学生事务中心管理系统 / University Student Affairs Center

## 📖 项目简介 / Project Overview

本项目是一个基于 Java Web 的高校学生事务管理系统，旨在为高校提供一个高效、便捷的学生事务线上处理平台。系统支持**管理员**、**教师**、**学生**三种角色，涵盖公告管理、学生请销假、公寓报修等核心功能模块。

This project is a Java Web-based university student affairs management system designed to provide an efficient and convenient online platform for handling student-related matters. The system supports three roles — **Admin**, **Teacher**, and **Student** — and covers core modules such as announcement management, student leave application/approval, and dormitory repair requests.

---

## ✨ 主要功能 / Key Features

### 🔔 公告管理 / Announcement Management
- 游客可浏览首页公告 / Visitors can view announcements on the homepage
- 管理员可发布、修改、删除公告 / Admin can create, edit, and delete announcements
- 学生与教师可查询公告详情 / Students and teachers can view announcement details

### 📝 请销假系统 / Leave Management
- 学生在线提交请假申请，系统自动获取申请人与申请时间 / Students submit leave applications; applicant info and time are auto-filled
- 教师审核本学院学生申请（通过/不通过） / Teachers review and approve/reject applications from their college
- 学生可在审核前撤销申请 / Students can withdraw applications before review
- 返校后在线销假 / Students confirm return and close the leave record

### 🔧 公寓报修系统 / Repair Request System
- 学生提交报修申请，支持图片上传 / Students submit repair requests with photo uploads
- 管理员审核并分配维修工 / Admin reviews requests and assigns maintenance staff
- 学生可在审核前撤销申请 / Students can withdraw requests before review
- 维修完成后学生可打分评价 / Students can rate and review the repair service after completion

---

## 👥 用户角色 / User Roles

| 角色 Role | 权限说明 Permissions |
|:---|:---|
| 游客 Visitor | 仅浏览主页公告 / View homepage announcements only |
| 学生 Student | 个人信息管理、发起请假/报修、销假、服务评价 / Profile management, leave & repair requests, check-in, service rating |
| 教师 Teacher | 个人信息管理、审核本学院学生请假申请 / Profile management, review leave requests from own college |
| 管理员 Admin | 个人信息管理、公告管理、报修审核与派单 / Profile management, announcement CRUD, repair request review & assignment |

---

## 🛠️ 技术栈 / Tech Stack

| 层级 Layer | 技术 Technology |
|:---|:---|
| 后端 Backend | Java, Spring Boot, Spring Security, Spring Data JPA / MyBatis-Plus |
| 前端 Frontend | Thymeleaf / Vue.js, Bootstrap / Element UI |
| 数据库 Database | MySQL |
| 构建工具 Build Tool | Maven / Gradle |

---

## 🚀 快速开始 / Quick Start

1. 克隆仓库 / Clone the repository
   ```bash
   git clone https://github.com/your-username/your-repo-name.git
