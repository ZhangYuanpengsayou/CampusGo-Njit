# CampusGo 当前接口与进度汇总

## 1. 当前项目状态

项目名称：CampusGo 高校学生事务中心

当前技术栈：

- 后端：Spring Boot 3.5.14
- Java：JDK 17
- 数据访问：MyBatis 3.0.5
- 数据库：MySQL 8.0.34
- 前端计划：HTML、CSS、JavaScript、Bootstrap、Vue
- 开发工具：IntelliJ IDEA

当前数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db-campusgo-njit
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 1234
```

当前数据库已初始化完成，包含以下表：

- `colleges`
- `majors`
- `class_groups`
- `dorm_buildings`
- `dorm_rooms`
- `users`
- `student_profiles`
- `teacher_profiles`
- `admin_profiles`
- `announcements`
- `carousel_items`
- `leave_applications`
- `repair_applications`

已完成后端基础能力：

- 统一响应类 `Result`
- 角色常量 `RoleConstants`
- 请假/报修状态常量 `StatusConstants`
- Session 常量 `SessionConstants`
- 登录用户对象 `LoginUser`
- 角色权限注解 `RequireRole`
- 全局异常处理 `GlobalExceptionHandler`
- 登录和权限拦截器 `AuthInterceptor`
- Web 配置 `WebMvcConfig`
- MyBatis Mapper 扫描和 XML 配置
- 文件上传大小配置

已完成业务模块：

- 学院列表查询
- 管理员新增学院
- 管理员修改学院
- 管理员新增、修改专业
- 管理员新增、修改班级
- 专业和班级按学院级联查询
- 管理员新增、修改公寓楼栋
- 管理员新增、修改宿舍号
- 宿舍号按公寓楼栋级联查询
- 用户注册，教师和管理员注册需填写申请码
- 用户登录
- 用户退出
- 当前用户信息查询
- 当前用户信息修改
- 当前用户密码修改
- 公告列表查询
- 公告详情查询
- 首页轮播图查询
- 管理员新增轮播图
- 管理员修改轮播图
- 管理员删除轮播图
- 管理员上传轮播图图片
- 管理员新增公告
- 管理员修改公告
- 管理员删除公告
- 学生提交请假申请
- 学生查询自己的请假申请
- 学生撤销待审核请假申请
- 学生对通过申请进行销假
- 教师查询本学院请假申请
- 教师审核本学院请假申请
- 学生上传报修照片
- 学生提交公寓报修申请
- 学生查询自己的报修申请
- 学生撤销待审核报修申请
- 管理员查询报修申请
- 管理员审核报修并填写维修工手机号
- 管理员将已通过报修标记为维修中
- 学生确认报修已完成
- 学生对已完成报修评分评价

已完成前端第一版：

- 使用 Vue + Bootstrap 搭建单页前端应用。
- 实现公共导航、首页公告、登录、注册、个人中心。
- 实现学生端请假、报修、撤销、销假、评价页面。
- 实现教师端请假审核页面。
- 实现管理员端公告管理、学院管理、报修审核页面。
- 使用统一 `campusgo.css` 保持页面风格一致。
- 首页已加入本地视觉素材 `static/assets/campus-hero.png`。
- 首页 Hero 已改为带指示器的轮播图，轮播图片由管理员维护。
- 导航左上角 Logo 已替换为 `static/assets/logo/campusgo-logo.jpeg`，点击可返回首页公告页。
- 首页登录后会隐藏“登录系统”和“创建账号”游客入口按钮。
- 注册页已在教师、管理员角色下显示申请码输入框。
- 注册页已增加确认密码，学生注册的学院、专业、班级、公寓楼栋、宿舍号均改为管理员预设下拉选项。
- 个人中心学生资料修改时，专业、班级、公寓楼栋、宿舍号同样使用系统预设下拉选项。
- 管理员端学院页已扩展为学院、专业、班级三级维护；宿舍页已支持楼栋和宿舍号维护。
- 业务页面已清理蓝色英文小标题，保留中文主标题。
- 列表页面已补充分页控件。
- 删除、撤销、销假、审核、维修状态更新等关键操作已补充确认提示。
- 删除、撤销、销假、审核、确认完成、维修中等关键操作已统一改为 Bootstrap 确认弹窗。
- 主要列表已补充空状态展示，包括请假、报修、审核、公告和学院列表。
- 管理员通过报修时改用 Bootstrap 模态框填写维修工手机号。
- 学生端可查看维修工手机号、确认完成并评价；管理员端可查看完成状态、评分和评价内容。

验证情况：

- `mvn test` 已通过
- Spring Boot 可正常启动
- MyBatis Mapper XML 已正常加载
- 已完成学生注册、登录、查询个人信息接口冒烟测试
- 已完成公告模块 Service 单元测试
- 已完成学院模块 Service 单元测试
- 已完成请销假模块 Service 单元测试
- 已完成公寓报修模块 Service 单元测试
- 已完成前端 JS 语法检查
- 已完成轮播图 Service 单元测试
- 已使用浏览器验证首页轮播、Logo 返回首页和管理员轮播管理入口
- 已使用浏览器验证统一确认弹窗和空列表状态
- 已完成教师/管理员注册申请码单元测试
- 已完成学生确认报修完成、管理员不能直接完成报修等单元测试
- 已使用浏览器验证首页、注册页、学院下拉和移动端基础布局
- 已使用接口联调完整跑通公告、请销假、公寓报修业务闭环
- 已使用浏览器验证学生端请假/报修数据展示和管理员端公告/学院/报修入口
- 已修正公告接口权限控制：游客仅可 GET 查询公告，新增、修改、删除必须登录且为管理员
- 已调整学院数据来源：学生和教师注册、修改资料时只能选择系统已启用学院，学院由管理员维护
- 已调整学生专业、班级、公寓楼栋和宿舍号数据来源：学生注册、修改资料时只能选择系统已启用选项，相关字典由管理员维护
- 已使用浏览器验证注册页学院-专业-班级、公寓楼栋-宿舍号级联下拉，以及管理员学院/宿舍维护入口

## 2. 统一响应格式

所有接口统一返回 JSON：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

常见 `code`：

| code | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 参数错误或业务错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 数据不存在 |
| 500 | 系统异常 |

## 3. 登录状态说明

当前项目使用 Session 保存登录状态。

登录成功后，后端会将 `LoginUser` 保存到 Session：

```java
session.setAttribute("currentUser", loginUser);
```

后续访问受保护接口时，需要浏览器或接口测试工具携带同一个 Session Cookie。

受保护接口：

- `/api/users/**`
- 后续的请假、报修、公告管理等接口

已放行接口：

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/announcements`
- `GET /api/announcements/{id}`

## 4. 已实现接口

## 4.1 用户注册

```text
POST /api/auth/register
```

权限：游客可访问。

用途：注册学生、教师、管理员账号。

### 学生注册请求示例

```json
{
  "username": "student001",
  "password": "123456",
  "confirmPassword": "123456",
  "role": "STUDENT",
  "realName": "张三",
  "phone": "13800000001",
  "email": "student@example.com",
  "college": "计算机学院",
  "studentNo": "20260001",
  "major": "软件工程",
  "className": "软件2401",
  "dormBuilding": "3栋",
  "dormRoom": "502"
}
```

### 教师注册请求示例

```json
{
  "username": "teacher001",
  "password": "123456",
  "confirmPassword": "123456",
  "role": "TEACHER",
  "realName": "李老师",
  "phone": "13800000002",
  "email": "teacher@example.com",
  "college": "计算机学院",
  "applicationCode": "TEACHER2026",
  "teacherNo": "T20260001",
  "title": "讲师",
  "office": "明德楼 302"
}
```

### 管理员注册请求示例

```json
{
  "username": "admin",
  "password": "123456",
  "confirmPassword": "123456",
  "role": "ADMIN",
  "realName": "系统管理员",
  "phone": "13800000003",
  "email": "admin@example.com",
  "applicationCode": "ADMIN2026",
  "adminNo": "A20260001",
  "department": "学生事务中心"
}
```

### 成功响应

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 主要业务规则

- `username` 不能为空且不能重复。
- `password` 不能为空。
- `confirmPassword` 必须与 `password` 一致。
- `role` 只能是 `STUDENT`、`TEACHER`、`ADMIN`。
- `realName` 不能为空。
- 学生注册时 `studentNo` 必填且不能重复。
- 学生注册时 `major`、`className`、`dormBuilding`、`dormRoom` 必须来自管理员维护的启用数据，不能自由填写。
- 教师注册时 `teacherNo` 必填且不能重复，并且 `applicationCode` 必须等于配置项 `campusgo.register.teacher-application-code`。
- 管理员注册时 `adminNo` 必填且不能重复，并且 `applicationCode` 必须等于配置项 `campusgo.register.admin-application-code`。
- 默认教师申请码为 `TEACHER2026`，默认管理员申请码为 `ADMIN2026`，可在 `application.yml` 中修改。
- 密码使用 SHA-256 加盐摘要后保存，不明文入库。

## 4.2 用户登录

```text
POST /api/auth/login
```

权限：游客可访问。

用途：账号密码登录，并写入 Session。

### 请求示例

```json
{
  "username": "student001",
  "password": "123456"
}
```

### 成功响应

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "student001",
    "role": "STUDENT",
    "realName": "张三",
    "college": "计算机学院"
  }
}
```

### 失败响应示例

```json
{
  "code": 400,
  "message": "账号或密码错误",
  "data": null
}
```

## 4.3 用户退出

```text
POST /api/auth/logout
```

权限：已登录用户。

用途：清除当前 Session。

### 成功响应

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

## 4.4 获取当前用户信息

```text
GET /api/users/me
```

权限：已登录用户。

用途：获取当前登录用户的基础信息和角色扩展信息。

### 学生响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "student001",
    "role": "STUDENT",
    "realName": "张三",
    "phone": "13800000001",
    "email": "student@example.com",
    "college": "计算机学院",
    "studentNo": "20260001",
    "major": "软件工程",
    "className": "软件2401",
    "dormBuilding": "3栋",
    "dormRoom": "502",
    "teacherNo": null,
    "title": null,
    "office": null,
    "adminNo": null,
    "department": null
  }
}
```

### 未登录响应

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null
}
```

## 4.5 修改当前用户信息

```text
PUT /api/users/me
```

权限：已登录用户。

用途：修改当前登录用户的个人资料。

### 学生请求示例

```json
{
  "realName": "张三",
  "phone": "13800000001",
  "email": "new-student@example.com",
  "college": "计算机学院",
  "major": "软件工程",
  "className": "软件2401",
  "dormBuilding": "5栋",
  "dormRoom": "608"
}
```

### 教师请求示例

```json
{
  "realName": "李老师",
  "phone": "13800000002",
  "email": "new-teacher@example.com",
  "college": "计算机学院",
  "title": "副教授",
  "office": "明德楼 508"
}
```

### 管理员请求示例

```json
{
  "realName": "系统管理员",
  "phone": "13800000003",
  "email": "new-admin@example.com",
  "department": "学生事务中心"
}
```

### 成功响应

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "username": "student001",
    "role": "STUDENT",
    "realName": "张三",
    "phone": "13800000001",
    "email": "new-student@example.com",
    "college": "计算机学院",
    "studentNo": "20260001",
    "major": "软件工程",
    "className": "软件2401",
    "dormBuilding": "5栋",
    "dormRoom": "608"
  }
}
```

说明：

- `username`、`role`、`studentNo`、`teacherNo`、`adminNo` 当前不允许通过该接口修改。
- 修改成功后，Session 中的当前用户基础信息会同步更新。

## 4.6 修改当前用户密码

```text
PUT /api/users/me/password
```

权限：已登录用户。

用途：修改当前登录用户密码。

### 请求示例

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

## 4.7 查询公告列表

```text
GET /api/announcements?page=1&pageSize=10&keyword=宿舍
```

权限：游客、学生、教师、管理员均可访问。

用途：查询未删除公告，按发布时间倒序展示。

### 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "title": "宿舍检修通知",
        "content": "本周进行宿舍水电检修。",
        "publisherId": 3,
        "publisherName": "系统管理员",
        "createdAt": "2026-06-07T10:00:00",
        "updatedAt": "2026-06-07T10:00:00"
      }
    ]
  }
}
```

## 4.8 查询公告详情

```text
GET /api/announcements/{id}
```

权限：游客、学生、教师、管理员均可访问。

用途：根据公告 ID 查询公告详情。

## 4.9 管理员新增公告

```text
POST /api/announcements
```

权限：管理员。

### 请求示例

```json
{
  "title": "宿舍检修通知",
  "content": "本周进行宿舍水电检修。"
}
```

### 成功响应

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "title": "宿舍检修通知",
    "content": "本周进行宿舍水电检修。",
    "publisherId": 3,
    "publisherName": "系统管理员"
  }
}
```

## 4.10 管理员修改公告

```text
PUT /api/announcements/{id}
```

权限：管理员。

### 请求示例

```json
{
  "title": "宿舍检修通知更新",
  "content": "检修时间调整为本周五。"
}
```

## 4.11 管理员删除公告

```text
DELETE /api/announcements/{id}
```

权限：管理员。

说明：当前为逻辑删除，将 `announcements.deleted` 更新为 `1`。

## 4.12 查询启用学院列表

```text
GET /api/colleges
```

权限：游客、学生、教师、管理员均可访问。

用途：注册页、个人信息页用于渲染学院下拉选择框。

## 4.13 管理员查询全部学院

```text
GET /api/colleges/manage
```

权限：管理员。

用途：管理员维护学院字典时查看启用和停用学院。

## 4.14 管理员新增学院

```text
POST /api/colleges
```

权限：管理员。

请求示例：

```json
{
  "name": "计算机学院",
  "description": "计算机相关专业学院",
  "status": 1
}
```

## 4.15 管理员修改学院

```text
PUT /api/colleges/{id}
```

权限：管理员。

说明：`status` 为 `1` 表示启用，`0` 表示停用。学生和教师只能选择启用学院。

## 4.15.1 查询启用专业列表

```text
GET /api/majors?college=计算机学院
```

权限：游客、学生、教师、管理员均可访问。

用途：注册页、个人信息页根据已选学院渲染专业下拉选择框。

## 4.15.2 管理员查询全部专业

```text
GET /api/majors/manage
```

权限：管理员。

用途：管理员维护学院下的专业字典时查看启用和停用专业。

## 4.15.3 管理员新增专业

```text
POST /api/majors
```

权限：管理员。

请求示例：

```json
{
  "collegeId": 1,
  "name": "软件工程",
  "status": 1
}
```

## 4.15.4 管理员修改专业

```text
PUT /api/majors/{id}
```

权限：管理员。

说明：`status` 为 `1` 表示启用，`0` 表示停用。学生只能选择启用专业。

## 4.15.5 查询启用班级列表

```text
GET /api/classes?college=计算机学院&major=软件工程
```

权限：游客、学生、教师、管理员均可访问。

用途：注册页、个人信息页根据已选学院和专业渲染班级下拉选择框。

## 4.15.6 管理员查询全部班级

```text
GET /api/classes/manage
```

权限：管理员。

用途：管理员维护专业下的班级字典时查看启用和停用班级。

## 4.15.7 管理员新增班级

```text
POST /api/classes
```

权限：管理员。

请求示例：

```json
{
  "majorId": 1,
  "name": "软件2401",
  "status": 1
}
```

## 4.15.8 管理员修改班级

```text
PUT /api/classes/{id}
```

权限：管理员。

说明：班级隶属于专业，专业隶属于学院。学生只能选择启用班级。

## 4.15.9 查询启用公寓楼栋列表

```text
GET /api/dorm-buildings
```

权限：游客、学生、教师、管理员均可访问。

用途：注册页、个人信息页渲染公寓楼栋下拉选择框。

## 4.15.10 管理员查询全部公寓楼栋

```text
GET /api/dorm-buildings/manage
```

权限：管理员。

用途：管理员维护公寓楼栋字典时查看启用和停用楼栋。

## 4.15.11 管理员新增公寓楼栋

```text
POST /api/dorm-buildings
```

权限：管理员。

请求示例：

```json
{
  "name": "3栋",
  "status": 1
}
```

## 4.15.12 管理员修改公寓楼栋

```text
PUT /api/dorm-buildings/{id}
```

权限：管理员。

说明：`status` 为 `1` 表示启用，`0` 表示停用。学生只能选择启用楼栋。

## 4.15.13 查询启用宿舍号列表

```text
GET /api/dorm-rooms?building=3栋
```

权限：游客、学生、教师、管理员均可访问。

用途：注册页、个人信息页根据已选公寓楼栋渲染宿舍号下拉选择框。

## 4.15.14 管理员查询全部宿舍号

```text
GET /api/dorm-rooms/manage
```

权限：管理员。

用途：管理员维护楼栋下的宿舍号字典时查看启用和停用宿舍号。

## 4.15.15 管理员新增宿舍号

```text
POST /api/dorm-rooms
```

权限：管理员。

请求示例：

```json
{
  "buildingId": 1,
  "roomNo": "502",
  "status": 1
}
```

## 4.15.16 管理员修改宿舍号

```text
PUT /api/dorm-rooms/{id}
```

权限：管理员。

说明：宿舍号隶属于公寓楼栋。学生只能选择启用宿舍号。

## 4.16 学生提交请假申请

```text
POST /api/leaves
```

权限：学生。

请求示例：

```json
{
  "reason": "生病就医",
  "startTime": "2026-06-10T08:00:00",
  "endTime": "2026-06-10T18:00:00"
}
```

说明：申请人、学院和申请时间由系统自动获取，初始状态为 `PENDING`。

## 4.17 学生查询自己的请假申请

```text
GET /api/leaves/my?page=1&pageSize=10&status=PENDING
```

权限：学生。

## 4.18 学生撤销请假申请

```text
PUT /api/leaves/{id}/cancel
```

权限：学生。

规则：只能撤销本人且状态为 `PENDING` 的申请。

## 4.19 学生销假

```text
PUT /api/leaves/{id}/return
```

权限：学生。

规则：只能对本人且状态为 `APPROVED` 的申请销假。

## 4.20 教师查询本学院请假申请

```text
GET /api/leaves/audit?page=1&pageSize=10&status=PENDING&studentName=张
```

权限：教师。

规则：后端根据教师所属学院过滤数据。

## 4.21 教师审核请假申请

```text
PUT /api/leaves/{id}/audit
```

权限：教师。

请求示例：

```json
{
  "status": "APPROVED",
  "auditOpinion": "同意请假"
}
```

规则：只能审核本学院且状态为 `PENDING` 的申请，审核状态只能为 `APPROVED` 或 `REJECTED`。

## 4.21.1 首页轮播图接口

### 查询启用轮播图

```text
GET /api/carousels
```

权限：游客、学生、教师、管理员均可访问。

用途：首页 Hero 轮播图展示，按 `sortOrder` 升序排列。

### 管理员查询全部轮播图

```text
GET /api/carousels/manage
```

权限：管理员。

### 管理员新增轮播图

```text
POST /api/carousels
```

权限：管理员。

请求示例：

```json
{
  "title": "校园事务，一站抵达。",
  "subtitle": "公告查看、请假申请、公寓报修和事务审核集中处理。",
  "imageUrl": "/uploads/carousel/20260607/demo.jpg",
  "sortOrder": 1,
  "status": 1
}
```

### 管理员修改轮播图

```text
PUT /api/carousels/{id}
```

权限：管理员。

### 管理员删除轮播图

```text
DELETE /api/carousels/{id}
```

权限：管理员。

### 管理员上传轮播图图片

```text
POST /api/files/carousel-image
Content-Type: multipart/form-data
```

权限：管理员。

表单字段：

```text
file
```

规则：仅支持 `jpg`、`jpeg`、`png`，文件大小不能超过 5MB。

## 4.22 学生上传报修照片

```text
POST /api/files/repair-photo
Content-Type: multipart/form-data
```

权限：学生。

表单字段：

```text
file
```

规则：

- 仅支持 `jpg`、`jpeg`、`png`。
- 文件大小不能超过 5MB。
- 上传成功后返回照片访问路径。

## 4.23 学生提交报修申请

```text
POST /api/repairs
```

权限：学生。

请求示例：

```json
{
  "reason": "宿舍水龙头漏水",
  "photoUrl": "/uploads/repair/20260607/photo.jpg"
}
```

说明：申请人、申请时间和宿舍信息由系统自动获取，初始状态为 `PENDING`。

## 4.24 学生查询自己的报修申请

```text
GET /api/repairs/my?page=1&pageSize=10&status=PENDING
```

权限：学生。

## 4.25 学生撤销报修申请

```text
PUT /api/repairs/{id}/cancel
```

权限：学生。

规则：只能撤销本人且状态为 `PENDING` 的报修申请。

## 4.26 管理员查询报修申请

```text
GET /api/repairs/audit?page=1&pageSize=10&status=PENDING&studentName=张&dormBuilding=3栋
```

权限：管理员。

## 4.27 管理员审核报修申请

```text
PUT /api/repairs/{id}/audit
```

权限：管理员。

请求示例：

```json
{
  "status": "APPROVED",
  "repairmanPhone": "13800000000",
  "auditOpinion": "已安排维修"
}
```

规则：

- 审核状态只能为 `APPROVED` 或 `REJECTED`。
- 审核通过时必须填写正确的维修工手机号。

## 4.28 管理员更新维修状态

```text
PUT /api/repairs/{id}/status
```

权限：管理员。

请求示例：

```json
{
  "status": "REPAIRING"
}
```

规则：

- 管理员只能将状态为 `APPROVED` 的报修更新为 `REPAIRING`。
- 管理员不再直接标记 `COMPLETED`，报修完成由学生确认。

## 4.29 学生确认报修完成

```text
PUT /api/repairs/{id}/complete
```

权限：学生。

规则：

- 只能确认本人报修。
- 只有状态为 `APPROVED` 或 `REPAIRING` 的报修可以确认完成。
- 确认后状态变为 `COMPLETED`。

## 4.30 学生评价报修

```text
PUT /api/repairs/{id}/rate
```

权限：学生。

请求示例：

```json
{
  "score": 5,
  "comment": "维修及时，服务很好"
}
```

规则：

- 只能评价本人且状态为 `COMPLETED` 的报修申请。
- 评分范围为 1 到 5。

### 成功响应

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 失败响应示例

```json
{
  "code": 400,
  "message": "原密码错误",
  "data": null
}
```

## 5. 已创建的主要代码文件

### 5.1 公共代码

- `src/main/java/com/zane/common/Result.java`
- `src/main/java/com/zane/common/annotation/RequireRole.java`
- `src/main/java/com/zane/common/constant/RoleConstants.java`
- `src/main/java/com/zane/common/constant/SessionConstants.java`
- `src/main/java/com/zane/common/constant/StatusConstants.java`
- `src/main/java/com/zane/common/session/LoginUser.java`
- `src/main/java/com/zane/common/util/PasswordUtil.java`
- `src/main/java/com/zane/exception/BusinessException.java`
- `src/main/java/com/zane/exception/GlobalExceptionHandler.java`
- `src/main/java/com/zane/config/AuthInterceptor.java`
- `src/main/java/com/zane/config/WebMvcConfig.java`

### 5.2 用户模块代码

- `src/main/java/com/zane/controller/AuthController.java`
- `src/main/java/com/zane/controller/UserController.java`
- `src/main/java/com/zane/service/AuthService.java`
- `src/main/java/com/zane/service/UserService.java`
- `src/main/java/com/zane/service/impl/AuthServiceImpl.java`
- `src/main/java/com/zane/service/impl/UserServiceImpl.java`
- `src/main/java/com/zane/mapper/UserMapper.java`
- `src/main/java/com/zane/mapper/StudentProfileMapper.java`
- `src/main/java/com/zane/mapper/TeacherProfileMapper.java`
- `src/main/java/com/zane/mapper/AdminProfileMapper.java`
- `src/main/resources/mapper/UserMapper.xml`
- `src/main/resources/mapper/StudentProfileMapper.xml`
- `src/main/resources/mapper/TeacherProfileMapper.xml`
- `src/main/resources/mapper/AdminProfileMapper.xml`

### 5.3 公告模块代码

- `src/main/java/com/zane/controller/AnnouncementController.java`
- `src/main/java/com/zane/service/AnnouncementService.java`
- `src/main/java/com/zane/service/impl/AnnouncementServiceImpl.java`
- `src/main/java/com/zane/mapper/AnnouncementMapper.java`
- `src/main/java/com/zane/entity/Announcement.java`
- `src/main/java/com/zane/dto/AnnouncementDTO.java`
- `src/main/java/com/zane/vo/AnnouncementVO.java`
- `src/main/java/com/zane/vo/PageVO.java`
- `src/main/resources/mapper/AnnouncementMapper.xml`
- `src/test/java/com/zane/service/impl/AnnouncementServiceImplTest.java`

### 5.4 学院模块代码

- `src/main/java/com/zane/controller/CollegeController.java`
- `src/main/java/com/zane/service/CollegeService.java`
- `src/main/java/com/zane/service/impl/CollegeServiceImpl.java`
- `src/main/java/com/zane/mapper/CollegeMapper.java`
- `src/main/java/com/zane/entity/College.java`
- `src/main/java/com/zane/dto/CollegeDTO.java`
- `src/main/java/com/zane/vo/CollegeVO.java`
- `src/main/resources/mapper/CollegeMapper.xml`
- `src/test/java/com/zane/service/impl/CollegeServiceImplTest.java`

### 5.5 请销假模块代码

- `src/main/java/com/zane/controller/LeaveController.java`
- `src/main/java/com/zane/service/LeaveService.java`
- `src/main/java/com/zane/service/impl/LeaveServiceImpl.java`
- `src/main/java/com/zane/mapper/LeaveApplicationMapper.java`
- `src/main/java/com/zane/entity/LeaveApplication.java`
- `src/main/java/com/zane/dto/LeaveCreateDTO.java`
- `src/main/java/com/zane/dto/LeaveAuditDTO.java`
- `src/main/java/com/zane/vo/LeaveApplicationVO.java`
- `src/main/resources/mapper/LeaveApplicationMapper.xml`
- `src/test/java/com/zane/service/impl/LeaveServiceImplTest.java`

### 5.6 公寓报修模块代码

- `src/main/java/com/zane/controller/FileController.java`
- `src/main/java/com/zane/controller/RepairController.java`
- `src/main/java/com/zane/service/FileStorageService.java`
- `src/main/java/com/zane/service/RepairService.java`
- `src/main/java/com/zane/service/impl/FileStorageServiceImpl.java`
- `src/main/java/com/zane/service/impl/RepairServiceImpl.java`
- `src/main/java/com/zane/mapper/RepairApplicationMapper.java`
- `src/main/java/com/zane/entity/RepairApplication.java`
- `src/main/java/com/zane/dto/RepairCreateDTO.java`
- `src/main/java/com/zane/dto/RepairAuditDTO.java`
- `src/main/java/com/zane/dto/RepairStatusDTO.java`
- `src/main/java/com/zane/dto/RepairRateDTO.java`
- `src/main/java/com/zane/vo/FileUploadVO.java`
- `src/main/java/com/zane/vo/RepairApplicationVO.java`
- `src/main/resources/mapper/RepairApplicationMapper.xml`
- `src/test/java/com/zane/service/impl/RepairServiceImplTest.java`

### 5.7 数据库脚本

- `src/main/resources/sql/campusgo.sql`
- `src/main/resources/sql/patch_colleges.sql`

### 5.8 前端代码

- `src/main/resources/static/index.html`
- `src/main/resources/static/css/campusgo.css`
- `src/main/resources/static/js/api.js`
- `src/main/resources/static/js/app.js`
- `src/main/resources/static/assets/campus-hero.png`

## 6. 下一步计划

已创建一组联调演示账号：

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 学生 | student_0607122036 | 123456 |
| 教师 | teacher_0607122036 | 123456 |
| 管理员 | admin_0607122036 | 123456 |

已跑通的联调流程：

- 管理员新增公告。
- 学生提交请假申请。
- 教师审核请假申请为通过。
- 学生完成销假。
- 学生提交公寓报修申请。
- 管理员审核报修并填写维修工手机号。
- 管理员将报修状态更新为维修中。
- 学生确认报修已完成。
- 学生完成报修评分评价。

下一步建议继续做页面细节打磨和演示准备：

1. 补充更细的空状态文案，例如无请假、无报修、无审核数据。
2. 将剩余浏览器原生 `confirm` 逐步替换为 Bootstrap 模态框，视觉更统一。
3. 增加真正的分页页码按钮和每页条数选择。
4. 增加接口请求时的局部加载状态。
5. 准备更多演示数据，让首页公告、审核列表和历史记录更丰富。
