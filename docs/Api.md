# TaskPie 后端接口文档

更新时间：2026-05-17  
基础地址：`http://localhost:8080/api`

## 1. 通用约定

### 1.1 认证方式

除 `/login`、`/register` 外，其余 `/api/**` 接口均需要在请求头中携带 JWT：

```http
Authorization: Bearer <token>
```

### 1.2 统一响应

普通 JSON 接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `code` | int | `200` 表示成功，其他为业务错误码 |
| `message` | string | 提示信息 |
| `data` | any | 返回数据；无数据时为 `null` |

分页响应结构：

```json
{
  "currentPage": 1,
  "items": [],
  "totalPages": 3,
  "totalItems": 25,
  "hasNext": true
}
```

注意：后端分页参数 `page` 从 `0` 开始，响应里的 `currentPage` 从 `1` 开始。

### 1.3 枚举值

系统角色 `role`：

| 值 | 含义 |
| --- | --- |
| `1` | 普通成员 |
| `100` | 系统管理员 |

小组角色 `teamRole`：

| 值 | 含义 |
| --- | --- |
| `1` | 小组成员 |
| `10` | 小组组长 |

通知类型 `notice.type`：

| 值 | 含义 |
| --- | --- |
| `10` | 系统全体通知 |
| `11` | 系统个人通知 |
| `20` | 小组全体通知 |
| `21` | 小组个人通知 |
| `30` | 小组邀请 |
| `31` | 加入申请 |

申请/邀请状态 `notice.status`：

| 值 | 含义 |
| --- | --- |
| `1` | 待处理 |
| `2` | 已同意 |
| `3` | 已拒绝 |

任务优先级 `task.priority`：

| 值 | 含义 |
| --- | --- |
| `1` | 低 |
| `2` | 普通 |
| `3` | 高 |
| `4` | 紧急 |

任务状态 `task.status`：

| 值 | 含义 |
| --- | --- |
| `1` | 待领取/待分配 |
| `2` | 进行中 |
| `3` | 待审核 |
| `4` | 已完成 |
| `5` | 已驳回 |

## 2. 账号认证 Auth

### 2.1 注册

`POST /register`

请求体：

```json
{
  "studentId": "20230001",
  "userName": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "nickName": "三三"
}
```

成功返回：`Response<Void>`

### 2.2 登录

`POST /login`

请求体：

```json
{
  "userName": "zhangsan",
  "password": "123456"
}
```

成功返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nickName": "三三",
    "role": 1,
    "token": "JWT_TOKEN"
  }
}
```

### 2.3 修改密码

`PATCH /password`

权限：登录用户

请求体：

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### 2.4 退出登录

`POST /logout`

权限：登录用户

说明：当前实现将 token 写入内存黑名单，服务重启后黑名单会丢失。

### 2.5 注销账号

`POST /close?password=123456`

权限：登录用户

说明：账号会被置为不可用，不会物理删除。

## 3. 用户 User

### 3.1 获取当前用户

`GET /user/me`

权限：登录用户

返回：

```json
{
  "userId": 1,
  "studentId": "20230001",
  "userName": "zhangsan",
  "avatar_url": null,
  "realName": "张三",
  "nickName": "三三",
  "bio": "个人简介",
  "role": 1,
  "createdAt": "2026-05-17T20:00:00",
  "updatedAt": "2026-05-17T20:00:00"
}
```

### 3.2 更新当前用户资料

`PUT /user/me`

请求体：

```json
{
  "studentId": "20230001",
  "realName": "张三",
  "nickName": "三三",
  "bio": "个人简介"
}
```

说明：已修复“不修改学号也被判定学号重复”的问题。

### 3.3 获取指定用户公开信息

`GET /user/{userId}`

权限：登录用户

### 3.4 搜索用户

`POST /user/select`

请求体：

```json
{
  "keyword": "张",
  "page": 0,
  "size": 10
}
```

说明：按学号精确匹配，或按真实姓名/昵称模糊匹配。

## 4. 小组 Team

### 4.1 创建小组

`POST /team/create`

权限：登录用户

请求体：

```json
{
  "name": "软件工程第23组",
  "description": "课程设计小组"
}
```

返回：新建小组 `teamId`

### 4.2 删除小组

`DELETE /team/{teamId}`

权限：小组组长

说明：删除小组及其成员关系。

### 4.3 获取小组详情

`GET /team/{teamId}`

权限：登录用户

### 4.4 获取我的小组

`GET /team/me`

权限：登录用户

返回：

```json
{
  "teams": [
    {
      "teamId": 1,
      "teamName": "软件工程第23组"
    }
  ]
}
```

### 4.5 搜索小组

`POST /team/select`

请求体：

```json
{
  "keyword": "软件工程",
  "page": 0,
  "size": 10
}
```

### 4.6 获取小组成员

`GET /team/{teamId}/member`

权限：小组成员

返回：成员列表，包含 `userId`、`studentId`、`realName`、`nickName`、`role`、`joinedAt`。

### 4.7 申请加入小组

`POST /team/{teamId}/apply`

权限：非小组成员

说明：创建一条 `type=31` 的申请通知，接收人为当前组长。

### 4.8 邀请成员

`POST /team/{teamId}/invite`

权限：小组组长

请求体：

```json
{
  "userId": 2
}
```

说明：创建一条 `type=30` 的邀请通知，接收人为被邀请用户。

### 4.9 移除成员

`DELETE /team/{teamId}/remove`

权限：小组组长

请求体：

```json
{
  "userIds": [2, 3]
}
```

说明：不能移除当前组长。

### 4.10 退出小组

`DELETE /team/{teamId}/quit`

权限：小组普通成员

说明：组长不能直接退出，需要先转让组长。

### 4.11 转让组长

`PATCH /team/{teamId}/transfer`

权限：小组组长

请求体：

```json
{
  "userId": 2
}
```

说明：目标用户必须已经是小组成员。

## 5. 通知 Notice

### 5.1 获取通知详情

`GET /notice/{noticeId}`

权限：通知接收人或相关小组成员

说明：首次读取时自动标记为已读。

### 5.2 查询通知列表

`POST /notice/select`

请求体：

```json
{
  "type": 0,
  "teamId": 0,
  "page": 0,
  "size": 10
}
```

参数说明：

| 字段 | 说明 |
| --- | --- |
| `type=0` | 查询当前用户可见的全部通知 |
| `type=1` | 系统/个人通知 |
| `type=2` | 小组通知；`teamId=0` 表示所有加入的小组 |
| `type=3` | 邀请/申请通知 |

### 5.3 处理邀请或申请

`PATCH /notice/{noticeId}/handle`

权限：通知接收人

请求体：

```json
{
  "accept": true,
  "feedback": "欢迎加入"
}
```

说明：

- 处理 `type=30` 邀请时，接收人同意后加入对应小组。
- 处理 `type=31` 申请时，组长同意后申请人加入对应小组。
- 只能处理 `status=1` 的待处理通知。

## 6. 任务 Task

### 6.1 创建任务

`POST /task/create`

权限：小组组长

请求体：

```json
{
  "teamId": 1,
  "assigneeId": 2,
  "title": "完成需求分析文档",
  "description": "整理用户故事和接口清单",
  "priority": 3,
  "deadline": "2026-05-30T23:59:59"
}
```

说明：

- `assigneeId` 可为空；为空时任务状态为 `1` 待领取。
- 指定 `assigneeId` 时，该用户必须是小组成员，任务状态为 `2` 进行中。

返回：新建任务 `taskId`

### 6.2 查询我的任务

`GET /task/me?status=2&page=0&size=10`

权限：登录用户

说明：查询当前用户被分配/领取的任务；`status` 可省略。

### 6.3 查询指定用户任务

`GET /task/user/{userId}?status=2&page=0&size=10`

权限：登录用户

说明：只能看到与当前用户同组范围内的该用户任务。

### 6.4 查询任务列表

`GET /task/list?teamId=1&status=2&page=0&size=10`

权限：小组成员

说明：

- `teamId` 可省略；省略时查询当前用户加入的所有小组任务。
- `status` 可省略；省略时查询全部状态。

### 6.5 获取任务详情

`GET /task/{taskId}`

权限：任务所属小组成员

返回：

```json
{
  "taskId": 1,
  "teamId": 1,
  "creatorId": 1,
  "assigneeId": 2,
  "title": "完成需求分析文档",
  "description": "整理用户故事和接口清单",
  "priority": 3,
  "status": 2,
  "deadline": "2026-05-30T23:59:59",
  "submitContent": null,
  "reviewFeedback": null,
  "attachmentName": null,
  "submittedAt": null,
  "reviewedAt": null,
  "createdAt": "2026-05-17T20:00:00",
  "updatedAt": "2026-05-17T20:00:00"
}
```

### 6.6 更新任务

`PUT /task/{taskId}`

权限：小组组长

请求体：

```json
{
  "assigneeId": 2,
  "title": "完成需求分析文档",
  "description": "补充原型截图",
  "priority": 4,
  "status": 2,
  "deadline": "2026-05-31T23:59:59"
}
```

说明：所有字段均可选，只更新传入字段。

### 6.7 删除任务

`DELETE /task/{taskId}`

权限：小组组长

### 6.8 领取任务

`PATCH /task/{taskId}/apply`

权限：任务所属小组成员

说明：只允许领取未被他人领取且未完成/未待审核的任务。

### 6.9 放弃任务

`PATCH /task/{taskId}/abandon`

权限：当前任务负责人

说明：任务回到 `1` 待领取状态。

### 6.10 提交任务

`POST /task/{taskId}/submit`

权限：当前任务负责人；组长也可代提交

请求体：

```json
{
  "content": "已完成文档，见附件。"
}
```

说明：任务状态变为 `3` 待审核。

### 6.11 审核任务

`PATCH /task/{taskId}/review`

权限：小组组长

请求体：

```json
{
  "passed": true,
  "feedback": "通过"
}
```

说明：

- `passed=true`：任务状态变为 `4` 已完成。
- `passed=false`：任务状态变为 `5` 已驳回，可重新提交。

### 6.12 上传任务附件

`POST /task/{taskId}/upload`

权限：当前任务负责人或小组组长

请求格式：`multipart/form-data`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `file` | file | 附件文件 |

说明：当前版本每个任务只记录最后一次上传的附件。

### 6.13 下载任务附件

`GET /task/{taskId}/download`

权限：任务所属小组成员

返回：文件流。该接口不使用统一 JSON 响应。

## 7. 错误码

| code | message | 说明 |
| --- | --- | --- |
| `10001` | 用户名已存在 | 注册用户名重复 |
| `10002` | 学号已存在 | 注册或修改资料时学号重复 |
| `10003` | 账号或密码错误 | 登录失败 |
| `10004` | 密码错误 | 修改密码/注销账号密码错误 |
| `20001` | 用户不存在 | 用户 ID 无效 |
| `30001` | 通知不存在 | 通知不存在或不可见 |
| `40001` | 小组不存在 | 小组 ID 无效 |
| `40002` | 不是小组成员 | 当前用户或目标用户不是小组成员 |
| `40003` | 用户已是小组成员 | 重复申请/邀请或重复加入 |
| `50001` | 任务不存在 | 任务 ID 无效 |
| `50002` | 任务已被领取 | 领取任务失败 |
| `50003` | 任务未分配 | 提交未分配任务 |
| `50004` | 任务状态不允许该操作 | 状态流转不合法 |
| `60001` | 参数错误 | 请求参数不合法 |
| `60002` | 权限不足 | 无权执行操作 |
| `60003` | 通知状态不允许该操作 | 重复处理邀请/申请 |
| `401` | 认证失败/账户异常 | token 缺失、无效或账号不可用 |

## 8. 最小演示流程

1. `POST /register` 注册两个用户。
2. `POST /login` 登录用户 A，复制 token。
3. 用户 A 调用 `POST /team/create` 创建小组。
4. 用户 A 调用 `POST /team/{teamId}/invite` 邀请用户 B。
5. 用户 B 登录后调用 `POST /notice/select` 查看邀请，再调用 `PATCH /notice/{noticeId}/handle` 同意。
6. 用户 A 调用 `POST /task/create` 创建并分配任务给用户 B。
7. 用户 B 调用 `GET /task/me` 查看任务，调用 `POST /task/{taskId}/submit` 提交。
8. 用户 A 调用 `PATCH /task/{taskId}/review` 审核通过。
