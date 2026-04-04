# Auth 账户相关

POST/login 登录

POST/logout 登出

POST/register 注册

PATCH/password 修改密码

PATCH/userName 修改用户名

POST/close 注销

# Admin

# User 用户相关

GET/user/me 获取当前用户信息

PUT/user/me 更新当前用户信息

POST/user/select 根据提示词搜索用户

GET/user/{userId} 获取指定用户信息

# Team 小组相关

POST/team/create 创建小组

GET/team/me 获取当前用户所在小组

GET/team/select 根据提示词搜索小组

GET/team/{teamId} 获取指定小组信息

DELETE/team/{teamId} 删除指定小组（组长）

## {teamId} 指定小组相关

GET/team/{teamId}/member 获取小组成员列表

DELETE/team/{teamId}/remove 移除成员（多个）（组长）

POST/team/{teamId}/invite 邀请成员（多个）（组长）

POST/team/{teamId}/apply 申请加入小组（非组员）

PATCH/team/{teamId}/transfer 转让组长（组长）

DELETE/teams/{teamId}/quit 退出小组（组员&非组长）
# Task 任务相关

POST/task/create 创建任务（组长）

GET/task/me 查看当前用户任务

GET/task/{userId} 查看指定用户任务

GET/task/list 查看所有任务

PUT/task/{taskId} 更新任务信息（组长）

DELETE/task/{taskId} 删除任务（组长）

GET/task/{taskId} 查看任务具体信息

## {taskId} 指定任务相关

PATCH/task/{taskId}/apply 申领任务

PATCH/task/{taskId}/abandon 放弃任务

POST/task/{taskId}/upload 上传附件

GET/task/{taskId}/download 下载附件

POST/task/{taskId}/submit 提交任务

# Notice 通知相关

POST/notice/create 创建通知

GET/notice/list 获取所有通知

GET/notice/{noticeId} 获取通知具体信息

PUT/notice/{noticeId} 更新通知状态

POST/notice/{noticeId} 申请/邀请类通知的同意与拒绝

# Upload 附件相关