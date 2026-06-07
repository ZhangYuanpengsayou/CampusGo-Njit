# CampusGo 用例图和流程图

## 1. 系统参与者

系统参与者包括：

- 游客：未登录用户，可浏览公告。
- 学生：提交请假和报修申请，维护个人信息。
- 教师：审核本学院学生请假申请。
- 管理员：管理公告，审核公寓报修申请。

## 2. 总体用例图

```mermaid
flowchart LR
    Guest["游客"]
    Student["学生"]
    Teacher["教师"]
    Admin["管理员"]

    UC1(("浏览公告"))
    UC2(("注册账号"))
    UC3(("登录系统"))
    UC4(("修改个人信息"))
    UC5(("提交请假申请"))
    UC6(("撤销请假申请"))
    UC7(("销假"))
    UC8(("审核请假申请"))
    UC9(("提交报修申请"))
    UC10(("撤销报修申请"))
    UC11(("评价报修"))
    UC12(("管理公告"))
    UC13(("审核报修申请"))

    Guest --> UC1
    Guest --> UC2
    Guest --> UC3

    Student --> UC1
    Student --> UC3
    Student --> UC4
    Student --> UC5
    Student --> UC6
    Student --> UC7
    Student --> UC9
    Student --> UC10
    Student --> UC11

    Teacher --> UC1
    Teacher --> UC3
    Teacher --> UC4
    Teacher --> UC8

    Admin --> UC1
    Admin --> UC3
    Admin --> UC12
    Admin --> UC13
```

## 3. 公告管理用例

```mermaid
flowchart LR
    Guest["游客"] --> Browse(("浏览公告"))
    Student["学生"] --> Browse
    Teacher["教师"] --> Browse
    Admin["管理员"] --> Browse
    Admin --> Add(("新增公告"))
    Admin --> Edit(("修改公告"))
    Admin --> Delete(("删除公告"))
```

## 4. 请销假业务流程图

```mermaid
flowchart TD
    A["学生登录"] --> B["填写请假理由、开始时间、结束时间"]
    B --> C["提交请假申请"]
    C --> D["系统生成申请人、学院、申请时间"]
    D --> E["状态: 待审核"]
    E --> F{教师是否已审核}
    F -->|否| G["学生可撤销申请"]
    G --> H["状态: 已撤销"]
    F -->|是| I{审核结果}
    I -->|通过| J["状态: 通过"]
    I -->|不通过| K["状态: 不通过"]
    J --> L["学生返校后销假"]
    L --> M["状态: 已销假"]
```

## 5. 教师审核请假流程图

```mermaid
flowchart TD
    A["教师登录"] --> B["进入请假审核页面"]
    B --> C["系统按教师学院查询申请"]
    C --> D["选择待审核申请"]
    D --> E["查看申请详情"]
    E --> F{审核决定}
    F -->|通过| G["填写审核意见并通过"]
    F -->|不通过| H["填写审核意见并拒绝"]
    G --> I["记录审核人和审核时间"]
    H --> I
    I --> J["通知或展示审核结果"]
```

## 6. 报修业务流程图

```mermaid
flowchart TD
    A["学生登录"] --> B["填写报修事由"]
    B --> C["上传报修照片"]
    C --> D["提交报修申请"]
    D --> E["系统生成申请人、申请时间、公寓信息"]
    E --> F["状态: 待审核"]
    F --> G{管理员是否已审核}
    G -->|否| H["学生可撤销申请"]
    H --> I["状态: 已撤销"]
    G -->|是| J{审核结果}
    J -->|通过| K["填写维修工手机号"]
    K --> L["状态: 通过或维修中"]
    J -->|不通过| M["状态: 不通过"]
    L --> N["维修完成"]
    N --> O["状态: 已完成"]
    O --> P["学生评分评价"]
    P --> Q["状态: 已评价"]
```

## 7. 管理员审核报修流程图

```mermaid
flowchart TD
    A["管理员登录"] --> B["进入报修审核页面"]
    B --> C["查询待审核报修申请"]
    C --> D["查看报修详情和照片"]
    D --> E{审核决定}
    E -->|通过| F["填写维修工手机号"]
    F --> G["保存审核结果"]
    E -->|不通过| H["填写审核意见"]
    H --> G
    G --> I["记录审核人和审核时间"]
```

## 8. 登录注册流程图

```mermaid
flowchart TD
    A["进入注册页面"] --> B["选择角色"]
    B --> C["填写账号、密码、姓名、手机号等信息"]
    C --> D["提交注册"]
    D --> E{账号或身份编号是否重复}
    E -->|是| F["提示注册失败"]
    E -->|否| G["保存用户和角色资料"]
    G --> H["注册成功"]
    H --> I["进入登录页面"]
    I --> J["输入账号和密码"]
    J --> K{校验是否通过}
    K -->|否| L["提示账号或密码错误"]
    K -->|是| M["根据角色进入对应首页"]
```

## 9. 状态流转图

### 9.1 请假状态流转

```mermaid
stateDiagram-v2
    [*] --> PENDING: 学生提交
    PENDING --> CANCELED: 学生撤销
    PENDING --> APPROVED: 教师通过
    PENDING --> REJECTED: 教师拒绝
    APPROVED --> RETURNED: 学生销假
    CANCELED --> [*]
    REJECTED --> [*]
    RETURNED --> [*]
```

### 9.2 报修状态流转

```mermaid
stateDiagram-v2
    [*] --> PENDING: 学生提交
    PENDING --> CANCELED: 学生撤销
    PENDING --> APPROVED: 管理员通过
    PENDING --> REJECTED: 管理员拒绝
    APPROVED --> REPAIRING: 开始维修
    REPAIRING --> COMPLETED: 维修完成
    COMPLETED --> RATED: 学生评价
    CANCELED --> [*]
    REJECTED --> [*]
    RATED --> [*]
```

