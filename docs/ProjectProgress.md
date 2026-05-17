# TaskPie 后端项目进度说明

更新时间：2026-05-17  
项目定位：高校小组协作任务管理系统后端  
当前结论：后端已经从“账号/用户/小组/通知基础模块”推进到“具备最小演示闭环”的状态。当前可以覆盖注册登录、小组创建、成员邀请/申请、通知处理、任务创建、任务领取/提交/审核、附件上传下载等核心流程。

## 1. 后端整体进度

| 模块 | 估计完成度 | 当前状态 |
| --- | --- | --- |
| 工程骨架 | 85% | Spring Boot、JPA、统一响应、全局异常、JWT 拦截、BCrypt、Validation 均已具备 |
| 账号认证 | 80% | 注册、登录、改密、退出、注销已实现；token 黑名单仍是内存级 |
| 用户模块 | 75% | 当前用户、用户资料更新、用户详情、用户搜索已实现；已修复学号不变时误判重复的问题 |
| 小组模块 | 75% | 创建、删除、详情、我的小组、搜索、成员列表、申请、邀请、移除、退出、转让组长已实现 |
| 通知模块 | 70% | 通知详情、列表、已读、邀请/申请处理已实现；已修复申请通知不落库的问题 |
| 任务模块 | 70% | 任务实体、仓库、服务、控制器、创建/列表/详情/更新/删除/领取/放弃/提交/审核已实现 |
| 附件模块 | 45% | 已支持任务附件上传/下载；目前每个任务只记录最后一次上传的附件 |
| 测试与质量 | 20% | 已做源码编译验证；缺少单元测试、集成测试和真实数据库联调 |
| 文档 | 75% | 已重写较完整接口文档 `docs/Api.md`，包含请求体、响应、权限、枚举、错误码和演示流程 |

粗略估计：后端完整度约 `70%~75%`。如果活动只要求提交并展示基本功能，目前后端已经能支撑一个可讲通的核心流程。

## 2. 本次已完善内容

### 2.1 任务模块从空类补成可用闭环

新增：

- `Task` 持久化实体
- `TaskStatus`、`TaskPriority` 枚举
- `TaskRepository`
- `TaskService`
- `TaskController`
- `data.dto.task` 下的任务请求/响应 DTO

已实现接口：

- `POST /api/task/create`
- `GET /api/task/me`
- `GET /api/task/user/{userId}`
- `GET /api/task/list`
- `GET /api/task/{taskId}`
- `PUT /api/task/{taskId}`
- `DELETE /api/task/{taskId}`
- `PATCH /api/task/{taskId}/apply`
- `PATCH /api/task/{taskId}/abandon`
- `POST /api/task/{taskId}/submit`
- `PATCH /api/task/{taskId}/review`
- `POST /api/task/{taskId}/upload`
- `GET /api/task/{taskId}/download`

任务状态流转：

`待领取/待分配 -> 进行中 -> 待审核 -> 已完成`  
审核驳回时：`待审核 -> 已驳回 -> 重新提交 -> 待审核`

### 2.2 小组模块补齐成员管理

新增/完善：

- 小组成员列表
- 申请加入小组
- 组长邀请成员
- 组长移除成员
- 成员退出小组
- 组长转让
- 删除小组权限从“任意成员”修正为“组长”

新增接口：

- `GET /api/team/{teamId}/member`
- `POST /api/team/{teamId}/apply`
- `POST /api/team/{teamId}/invite`
- `DELETE /api/team/{teamId}/remove`
- `DELETE /api/team/{teamId}/quit`
- `PATCH /api/team/{teamId}/transfer`

### 2.3 通知模块形成申请/邀请闭环

修复：

- `NoticeCreate.application(...)` 原来只构造通知但没有 `save`，申请通知不会入库；现已修复。

新增：

- `PATCH /api/notice/{noticeId}/handle`

处理规则：

- 用户同意邀请后加入小组。
- 组长同意申请后申请人加入小组。
- 邀请/申请只能处理一次。

### 2.4 环境和演示友好性修正

- 后端 Java 编译版本从 `21` 调整为 `17`，适配当前机器的 JDK 17。
- 修复 `application.yml` 中 `spring.jmx.enabled` 的 YAML 写法。
- `TeamMemberRepository` 的主键泛型从 `Integer` 修正为复合主键 `TeamMemberId`。
- `SelectNoticeRequest` 移除不适合 Jackson 反序列化的 `@Builder`。

## 3. 当前可演示流程

推荐按下面的最小闭环演示：

1. 注册用户 A 和用户 B。
2. 用户 A 登录，创建小组。
3. 用户 A 邀请用户 B 加入小组。
4. 用户 B 登录，查看通知并同意邀请。
5. 用户 A 创建任务并分配给用户 B。
6. 用户 B 查看我的任务，提交任务成果。
7. 用户 A 审核通过任务。
8. 双方查看任务状态和通知列表。

这一套流程已经覆盖账号、小组、通知、任务四个主要模块，适合作为活动提交说明。

## 4. 仍未完成或质量较弱的地方

| 风险 | 影响 | 建议 |
| --- | --- | --- |
| 缺少真实数据库联调 | 编译通过不等于运行时完全可用 | 用 MySQL 建库后按接口文档跑一遍 Postman/Apifox |
| 测试覆盖不足 | 后续改动容易回归 | 至少补 Auth、Team、Task 的服务层测试 |
| 附件模型简单 | 每个任务只记录最后一个附件 | 后续可拆成 `task_attachment` 表 |
| 通知内容未完全格式化 | 邀请/申请通知内容仍较简略 | 可在通知创建时拼接用户昵称和小组名 |
| 权限模型偏粗 | 只有系统角色和小组组长/成员 | 课程设计够用，后续可加管理员接口 |
| 删除小组未级联任务 | 删除小组后历史任务可能残留 | 真实上线前应加软删除或级联处理 |
| API 尚无自动化 Swagger | 文档需要手动维护 | 后续可接入 springdoc-openapi |

## 5. 构建验证

本次执行过两类验证。

源码编译：

```powershell
mvn "-Dmaven.repo.local=E:\协同码力·校园编程实战计划\project\.m2_repo" compile
```

结果：

- `BUILD SUCCESS`
- 编译了 `66` 个 Java 源文件
- 当前使用 JDK `17.0.8`

完整测试：

```powershell
mvn "-Dmaven.repo.local=E:\协同码力·校园编程实战计划\project\.m2_repo" clean test
```

结果：

- `BUILD SUCCESS`
- `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`
- Spring Boot 上下文启动成功，并成功连接配置中的 MySQL 数据库。

备注：第一次执行 `mvn test` 时，由于 `target/test-classes` 里存在旧的 Java 21 编译产物而失败；执行 `clean test` 清理后已通过。

## 6. 后续最省时间的补强建议

1. 使用 Postman/Apifox 按 `docs/Api.md` 的“最小演示流程”录入测试数据。
2. 准备两到三个演示账号，提前创建一个小组和几条任务。
3. 如果需要前端联调，优先接入登录、我的小组、通知列表、任务列表、任务提交、任务审核。
4. 如果老师重点看完整性，提交材料里突出“账号-小组-通知-任务”的闭环。
