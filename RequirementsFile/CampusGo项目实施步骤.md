# CampusGo 项目实施步骤

## 1. 开发环境准备

### 1.1 工具准备

建议安装并确认以下环境：

| 工具 | 建议版本 | 用途 |
| --- | --- | --- |
| IntelliJ IDEA | 2024 或更新版本 | 项目开发与管理 |
| JDK | 17 | Spring Boot 运行环境 |
| Maven | IDEA 内置或本机安装 | 项目构建 |
| MySQL | 8.x | 数据库 |
| Navicat 或 DataGrip | 可选 | 数据库可视化管理 |
| Chrome 或 Edge | 最新版 | 页面测试 |

### 1.2 项目技术栈

项目采用以下技术：

- 后端：Spring Boot 3、Spring MVC、MyBatis。
- 数据库：MySQL。
- 构建工具：Maven。
- 前端：HTML、CSS、JavaScript、Bootstrap、Vue。
- 开发工具：IntelliJ IDEA。

## 2. IDEA 导入项目

1. 打开 IntelliJ IDEA。
2. 选择 `Open`。
3. 打开项目目录：

```text
E:\foreign lessons\JavaWeb\Project\CampusGo-Njit\CampusGo
```

4. 等待 IDEA 自动识别 Maven 项目并下载依赖。
5. 确认 Project SDK 设置为 JDK 17。
6. 打开 `pom.xml`，确认已有依赖包括：

- `spring-boot-starter-web`
- `mybatis-spring-boot-starter`
- `mysql-connector-j`
- `lombok`
- `spring-boot-starter-test`

## 3. 数据库初始化

### 3.1 创建数据库

使用 MySQL 用户：

```text
用户名：root
密码：1234
```

执行 SQL：

```sql
CREATE DATABASE IF NOT EXISTS campusgo
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 3.2 创建数据表

根据文档 [CampusGo数据库表结构设计.md](<E:\foreign lessons\JavaWeb\Project\CampusGo-njit\RequirementsFile\CampusGo数据库表结构设计.md>) 中的 SQL，依次创建以下表：

- `user`
- `student_profile`
- `teacher_profile`
- `admin_profile`
- `announcement`
- `leave_application`
- `repair_application`

建议在项目中新增 SQL 文件：

```text
src/main/resources/sql/campusgo.sql
```

将建库和建表语句统一保存，方便后续重复初始化。

## 4. 配置 application.yml

打开：

```text
src/main/resources/application.yml
```

配置本地 MySQL 连接：

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/campusgo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 1234

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.zane.entity
  configuration:
    map-underscore-to-camel-case: true
```

说明：课程项目可以直接使用本地密码配置。正式项目中不建议把数据库密码直接写在代码仓库里。

## 5. 后端包结构搭建

在 `src/main/java/com/zane` 下创建以下包：

```text
com.zane
  ├─ common
  ├─ config
  ├─ controller
  ├─ dto
  ├─ entity
  ├─ exception
  ├─ mapper
  ├─ service
  │   └─ impl
  └─ vo
```

各包职责：

| 包名 | 职责 |
| --- | --- |
| `entity` | 数据库实体类 |
| `dto` | 接收前端请求参数 |
| `vo` | 返回前端展示数据 |
| `mapper` | MyBatis 数据访问接口 |
| `service` | 业务接口 |
| `service.impl` | 业务实现 |
| `controller` | Web 接口 |
| `common` | 统一响应、常量、枚举 |
| `config` | Web 配置、文件上传配置 |
| `exception` | 自定义异常和统一异常处理 |

## 6. 第一阶段：公共基础代码

### 6.1 统一响应结果

创建 `Result<T>`，统一接口返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 6.2 角色和状态常量

创建角色常量：

- `STUDENT`
- `TEACHER`
- `ADMIN`

创建请假状态常量：

- `PENDING`
- `APPROVED`
- `REJECTED`
- `CANCELED`
- `RETURNED`

创建报修状态常量：

- `PENDING`
- `APPROVED`
- `REJECTED`
- `CANCELED`
- `REPAIRING`
- `COMPLETED`
- `RATED`

### 6.3 登录拦截器

实现登录拦截器：

- 放行首页公告查询接口。
- 放行登录、注册接口。
- 其他接口必须登录。
- 根据 Session 中的用户角色判断访问权限。

## 7. 第二阶段：用户注册登录模块

### 7.1 实体类

创建：

- `User`
- `StudentProfile`
- `TeacherProfile`
- `AdminProfile`

### 7.2 Mapper

创建：

- `UserMapper`
- `StudentProfileMapper`
- `TeacherProfileMapper`
- `AdminProfileMapper`

完成用户新增、按账号查询、按 ID 查询、修改个人信息等 SQL。

### 7.3 Service

实现：

- 注册。
- 登录。
- 退出。
- 获取当前用户。
- 修改个人信息。
- 修改密码。

### 7.4 Controller

实现接口：

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/users/me`
- `PUT /api/users/me`
- `PUT /api/users/me/password`

### 7.5 验证重点

- 账号不能重复。
- 学号、工号、管理员编号不能重复。
- 登录成功后按角色进入不同首页。
- 未登录不能访问个人中心。

## 8. 第三阶段：公告模块

### 8.1 后端开发

创建：

- `Announcement` 实体类。
- `AnnouncementMapper`。
- `AnnouncementService`。
- `AnnouncementController`。

实现接口：

- `GET /api/announcements`
- `GET /api/announcements/{id}`
- `POST /api/announcements`
- `PUT /api/announcements/{id}`
- `DELETE /api/announcements/{id}`

### 8.2 前端页面

使用 Bootstrap 完成：

- 首页公告卡片。
- 公告列表。
- 公告详情。
- 管理员公告管理表格。
- 新增和编辑公告表单。

### 8.3 验证重点

- 游客可以查看公告。
- 学生和教师只能查询公告。
- 只有管理员可以新增、修改、删除公告。

## 9. 第四阶段：学生请销假模块

### 9.1 后端开发

创建：

- `LeaveApplication` 实体类。
- `LeaveApplicationMapper`。
- `LeaveService`。
- `LeaveController`。

实现接口：

- `POST /api/leaves`
- `GET /api/leaves/my`
- `PUT /api/leaves/{id}/cancel`
- `PUT /api/leaves/{id}/return`
- `GET /api/leaves/audit`
- `PUT /api/leaves/{id}/audit`

### 9.2 业务规则

- 学生提交后状态为 `PENDING`。
- 申请人、学院、申请时间由系统自动获取。
- 只有 `PENDING` 状态可以撤销。
- 教师只能查看和审核本学院学生申请。
- 教师只能将待审核申请改为 `APPROVED` 或 `REJECTED`。
- 学生只能对 `APPROVED` 状态申请销假。

### 9.3 前端页面

使用 Bootstrap 完成：

- 学生请假申请列表。
- 新建请假表单。
- 请假详情。
- 教师请假审核列表。
- 教师请假审核弹窗或详情页。

### 9.4 验证重点

- 请假开始时间不能晚于结束时间。
- 学生不能撤销已审核申请。
- 教师不能审核其他学院申请。
- 学生销假后状态正确变为 `RETURNED`。

## 10. 第五阶段：公寓报修模块

### 10.1 后端开发

创建：

- `RepairApplication` 实体类。
- `RepairApplicationMapper`。
- `RepairService`。
- `RepairController`。
- `FileController`。

实现接口：

- `POST /api/files/repair-photo`
- `POST /api/repairs`
- `GET /api/repairs/my`
- `PUT /api/repairs/{id}/cancel`
- `GET /api/repairs/audit`
- `PUT /api/repairs/{id}/audit`
- `PUT /api/repairs/{id}/status`
- `PUT /api/repairs/{id}/rate`

### 10.2 文件上传

建议图片保存目录：

```text
E:\foreign lessons\JavaWeb\Project\CampusGo-Njit\CampusGo\uploads\repair
```

数据库只保存图片访问路径，例如：

```text
/uploads/repair/20260607/photo.jpg
```

上传限制：

- 只允许 `jpg`、`jpeg`、`png`。
- 文件大小建议不超过 5MB。

### 10.3 业务规则

- 学生提交后状态为 `PENDING`。
- 管理员审核通过时必须填写维修工手机号。
- 维修工手机号必须符合手机号格式。
- 学生只能撤销自己的待审核报修。
- 学生只能评价自己的已完成报修。
- 同一报修申请只能评价一次。

### 10.4 前端页面

使用 Bootstrap 完成：

- 学生报修列表。
- 新建报修表单。
- 图片上传预览。
- 管理员报修审核列表。
- 管理员报修审核详情。
- 学生评分评价表单。

## 11. 第六阶段：前端页面整合

### 11.1 页面目录建议

如果使用 HTML + Bootstrap + Vue，可在 `src/main/resources/static` 下创建：

```text
static
  ├─ css
  │   └─ campusgo.css
  ├─ js
  │   ├─ api.js
  │   ├─ app.js
  │   └─ campusgo.js
  ├─ pages
  │   ├─ login.html
  │   ├─ register.html
  │   ├─ student.html
  │   ├─ teacher.html
  │   └─ admin.html
  └─ index.html
```

说明：

- `api.js` 统一封装 `fetch` 请求、Session Cookie 携带和错误提示。
- `app.js` 负责 Vue 应用入口、公共状态和角色菜单切换。
- 各页面可继续使用独立 HTML 文件，也可逐步整理为 Vue 组件式结构。

### 11.2 Bootstrap 引入方式

课程项目可使用 CDN：

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://unpkg.com/vue@3/dist/vue.global.prod.js"></script>
```

如果希望离线运行，可下载 Bootstrap 文件放入：

```text
static/lib/bootstrap
static/lib/vue
```

### 11.3 Vue 使用建议

- 用 Vue 维护登录用户、学院列表、公告列表、请假列表、报修列表等页面状态。
- 用 `v-for` 渲染公告卡片、表格行和下拉选项。
- 用 `v-if` 或 `v-show` 控制不同角色的菜单和操作按钮。
- 用 `v-model` 处理登录、注册、请假、报修、审核、评价等表单。
- 接口调用统一经过 `api.js`，不要在每个页面重复写请求处理逻辑。

### 11.4 页面风格实现

在 `campusgo.css` 中覆盖 Bootstrap 默认样式：

- 页面背景使用 `#f5f5f7`。
- 卡片圆角使用 `20px` 左右。
- 主按钮使用苹果蓝 `#0071e3`。
- 页面顶部导航保持简洁。
- 表格和表单保留充足留白。
- 所有页面使用统一导航栏、统一按钮样式、统一表单输入框、统一状态标签和统一表格密度。
- 学生端、教师端、管理员端可以内容不同，但布局节奏、字体层级、颜色、圆角和间距必须保持一致。
- 禁止每个页面单独定义一套颜色和按钮风格；公共视觉变量集中写在 `campusgo.css`。

## 12. 第七阶段：联调与测试

按照 [CampusGo测试用例设计.md](<E:\foreign lessons\JavaWeb\Project\CampusGo-njit\RequirementsFile\CampusGo测试用例设计.md>) 逐项测试。

测试顺序建议：

1. 数据库连接测试。
2. 注册登录测试。
3. 游客公告查看测试。
4. 管理员公告管理测试。
5. 学生请假申请测试。
6. 教师请假审核测试。
7. 学生销假测试。
8. 学生报修申请测试。
9. 管理员报修审核测试。
10. 学生报修评价测试。
11. 权限越权测试。

## 13. 第八阶段：运行与演示

### 13.1 IDEA 启动

在 IDEA 中打开：

```text
src/main/java/com/zane/CampusGoApplication.java
```

点击运行按钮启动项目。

默认访问地址：

```text
http://localhost:8080
```

### 13.2 演示流程建议

演示时建议按以下顺序：

1. 游客进入首页查看公告。
2. 管理员登录，新增一条公告。
3. 学生登录，查看公告并修改个人信息。
4. 学生提交请假申请。
5. 教师登录，审核该请假申请。
6. 学生登录，完成销假。
7. 学生提交公寓报修并上传照片。
8. 管理员登录，审核报修并填写维修工手机号。
9. 管理员将报修状态改为已完成。
10. 学生对报修进行评分评价。

## 14. 开发进度安排建议

| 阶段 | 内容 | 建议时间 |
| --- | --- | --- |
| 第 1 天 | 环境搭建、数据库建表、配置连接 | 0.5 到 1 天 |
| 第 2 天 | 用户注册登录、Session、权限拦截 | 1 天 |
| 第 3 天 | 公告模块后端和页面 | 1 天 |
| 第 4 天 | 请假模块后端和页面 | 1 到 1.5 天 |
| 第 5 天 | 报修模块后端、文件上传和页面 | 1.5 天 |
| 第 6 天 | Bootstrap 页面美化和角色首页整合 | 1 天 |
| 第 7 天 | 测试、修复问题、准备演示数据 | 1 天 |

## 15. 实施优先级

优先完成核心闭环：

1. 用户注册登录。
2. 公告查询与管理。
3. 学生请假申请。
4. 教师审核请假。
5. 学生报修申请。
6. 管理员审核报修。
7. 学生销假和报修评价。

可以后续优化的内容：

- 更精细的搜索筛选。
- 分页组件美化。
- 上传图片压缩。
- 操作日志。
- 消息通知。
- 更完整的管理员用户管理。
