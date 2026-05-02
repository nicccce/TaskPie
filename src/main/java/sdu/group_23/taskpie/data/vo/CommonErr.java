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
    NOTICE_NOT_FOUND(50001, "通知不存在"),

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
