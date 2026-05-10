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

    //Task

    //System
    PARAM_ERROR(60001, "参数错误"),
    PERMISSION_DENIED(60002, "权限不足"),

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
