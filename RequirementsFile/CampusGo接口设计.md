# CampusGo 接口设计

## 1. 接口设计说明

接口采用 REST 风格，数据格式为 JSON。文件上传使用 `multipart/form-data`。

基础路径建议：

```text
/api
```

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

常见状态码：

| code | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 数据不存在 |
| 409 | 状态冲突或重复提交 |
| 500 | 系统异常 |

## 2. 认证接口

### 2.1 注册

```text
POST /api/auth/register
```

请求参数：

```json
{
  "username": "student001",
  "password": "123456",
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

响应：

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 2.2 登录

```text
POST /api/auth/login
```

请求参数：

```json
{
  "username": "student001",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "student001",
    "role": "STUDENT",
    "realName": "张三"
  }
}
```

### 2.3 退出登录

```text
POST /api/auth/logout
```

## 3. 用户接口

说明：学生、教师注册和修改个人信息时，`college` 必须来自系统已启用学院列表，不允许自由填写。

## 3.1 学院接口

### 查询启用学院列表

```text
GET /api/colleges
```

权限：游客、学生、教师、管理员。

### 管理员查询全部学院

```text
GET /api/colleges/manage
```

权限：管理员。

### 管理员新增学院

```text
POST /api/colleges
```

权限：管理员。

```json
{
  "name": "计算机学院",
  "description": "计算机相关专业学院",
  "status": 1
}
```

### 管理员修改学院

```text
PUT /api/colleges/{id}
```

权限：管理员。

### 3.2 获取当前用户信息

```text
GET /api/users/me
```

权限：学生、教师、管理员。

### 3.3 修改当前用户信息

```text
PUT /api/users/me
```

请求参数：

```json
{
  "realName": "张三",
  "phone": "13800000001",
  "email": "student@example.com",
  "college": "计算机学院"
}
```

### 3.4 修改密码

```text
PUT /api/users/me/password
```

请求参数：

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

## 4. 公告接口

### 4.1 查询公告列表

```text
GET /api/announcements?page=1&pageSize=10&keyword=宿舍
```

权限：游客、学生、教师、管理员。

响应：

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
        "publisherName": "系统管理员",
        "createdAt": "2026-06-07 10:00:00"
      }
    ]
  }
}
```

### 4.2 查询公告详情

```text
GET /api/announcements/{id}
```

### 4.3 新增公告

```text
POST /api/announcements
```

权限：管理员。

请求参数：

```json
{
  "title": "宿舍检修通知",
  "content": "本周进行宿舍水电检修。"
}
```

### 4.4 修改公告

```text
PUT /api/announcements/{id}
```

权限：管理员。

### 4.5 删除公告

```text
DELETE /api/announcements/{id}
```

权限：管理员。

## 5. 请假接口

### 5.1 学生提交请假申请

```text
POST /api/leaves
```

权限：学生。

请求参数：

```json
{
  "reason": "生病就医",
  "startTime": "2026-06-10 08:00:00",
  "endTime": "2026-06-11 18:00:00"
}
```

### 5.2 学生查询自己的请假申请

```text
GET /api/leaves/my?page=1&pageSize=10&status=PENDING
```

权限：学生。

### 5.3 学生撤销请假申请

```text
PUT /api/leaves/{id}/cancel
```

权限：学生。

规则：只有本人且状态为 `PENDING` 的申请可以撤销。

### 5.4 学生销假

```text
PUT /api/leaves/{id}/return
```

权限：学生。

规则：只有本人且状态为 `APPROVED` 的申请可以销假。

### 5.5 教师查询本学院请假申请

```text
GET /api/leaves/audit?page=1&pageSize=10&status=PENDING&studentName=张
```

权限：教师。

规则：后端根据教师所属学院过滤数据。

### 5.6 教师审核请假申请

```text
PUT /api/leaves/{id}/audit
```

权限：教师。

请求参数：

```json
{
  "status": "APPROVED",
  "auditOpinion": "同意请假"
}
```

规则：

- `status` 只能为 `APPROVED` 或 `REJECTED`。
- 只能审核本学院学生的待审核申请。

## 6. 报修接口

### 6.1 上传报修照片

```text
POST /api/files/repair-photo
Content-Type: multipart/form-data
```

权限：学生。

表单字段：

```text
file
```

响应：

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/uploads/repair/20260607/photo.jpg"
  }
}
```

### 6.2 学生提交报修申请

```text
POST /api/repairs
```

权限：学生。

请求参数：

```json
{
  "reason": "宿舍水龙头漏水",
  "photoUrl": "/uploads/repair/20260607/photo.jpg"
}
```

### 6.3 学生查询自己的报修申请

```text
GET /api/repairs/my?page=1&pageSize=10&status=PENDING
```

权限：学生。

### 6.4 学生撤销报修申请

```text
PUT /api/repairs/{id}/cancel
```

权限：学生。

规则：只有本人且状态为 `PENDING` 的申请可以撤销。

### 6.5 管理员查询报修申请

```text
GET /api/repairs/audit?page=1&pageSize=10&status=PENDING&dormBuilding=3栋
```

权限：管理员。

### 6.6 管理员审核报修申请

```text
PUT /api/repairs/{id}/audit
```

权限：管理员。

请求参数：

```json
{
  "status": "APPROVED",
  "repairmanPhone": "13800000002",
  "auditOpinion": "已安排维修"
}
```

规则：

- `status` 只能为 `APPROVED` 或 `REJECTED`。
- 审核通过时 `repairmanPhone` 必填。

### 6.7 管理员更新维修状态

```text
PUT /api/repairs/{id}/status
```

权限：管理员。

请求参数：

```json
{
  "status": "COMPLETED"
}
```

### 6.8 学生评价报修

```text
PUT /api/repairs/{id}/rate
```

权限：学生。

请求参数：

```json
{
  "score": 5,
  "comment": "维修及时，服务很好"
}
```

规则：

- 只有本人且状态为 `COMPLETED` 的申请可以评价。
- 评分范围为 1 到 5。

当前后端已实现公寓报修模块接口：

- `POST /api/files/repair-photo`
- `POST /api/repairs`
- `GET /api/repairs/my`
- `PUT /api/repairs/{id}/cancel`
- `GET /api/repairs/audit`
- `PUT /api/repairs/{id}/audit`
- `PUT /api/repairs/{id}/status`
- `PUT /api/repairs/{id}/rate`

## 7. 分页参数约定

| 参数 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| page | int | 1 | 当前页 |
| pageSize | int | 10 | 每页条数 |

## 8. 接口权限汇总

| 接口 | 游客 | 学生 | 教师 | 管理员 |
| --- | --- | --- | --- | --- |
| 查询公告 | 支持 | 支持 | 支持 | 支持 |
| 管理公告 | 不支持 | 不支持 | 不支持 | 支持 |
| 个人信息 | 不支持 | 支持 | 支持 | 支持 |
| 请假申请 | 不支持 | 支持 | 不支持 | 不支持 |
| 请假审核 | 不支持 | 不支持 | 支持 | 不支持 |
| 报修申请 | 不支持 | 支持 | 不支持 | 不支持 |
| 报修审核 | 不支持 | 不支持 | 不支持 | 支持 |
