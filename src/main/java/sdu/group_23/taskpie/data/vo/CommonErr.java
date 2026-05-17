package sdu.group_23.taskpie.data.vo;

import lombok.Getter;

@Getter
public enum CommonErr {

    //Auth
    USERNAME_ALREADY_EXISTS(10001, "用户名已存在"),
    STUDENT_ID_ALREADY_EXISTS(10002, "学号已存在"),
    LOGIN_FAILURE(10003, "账号或密码错误"),
    PASSWORD_WRONG(10004, "密码错误"),

    //user
    USER_NOT_FOUND(20001, "用户不存在"),

    //Notice
    NOTICE_NOT_FOUND(30001, "通知不存在"),

    //Team
    TEAM_NOT_FOUND(40001, "小组不存在"),
    NOT_TEAM_MEMBER(40002, "不是小组成员"),
    TEAM_MEMBER_ALREADY_EXISTS(40003, "用户已是小组成员"),

    //Task
    TASK_NOT_FOUND(50001, "任务不存在"),
    TASK_ALREADY_ASSIGNED(50002, "任务已被领取"),
    TASK_NOT_ASSIGNED(50003, "任务未分配"),
    TASK_STATUS_INVALID(50004, "任务状态不允许该操作"),

    //System
    PARAM_ERROR(60001, "参数错误"),
    PERMISSION_DENIED(60002, "权限不足"),
    NOTICE_STATUS_INVALID(60003, "通知状态不允许该操作"),

    //Jwt
    UNAUTHORIZED(401, "认证失败"),
    AUTH_EXCEPTION(401, "账户异常");

    private final int code;
    private final String message;

    CommonErr(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
