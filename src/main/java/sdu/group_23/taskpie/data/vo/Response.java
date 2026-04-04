package sdu.group_23.taskpie.data.vo;

import lombok.Getter;

@Getter
public class Response<T> {

    private int code;
    private String message;
    private T data;

    Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static<T> Response<T> ok() { return  new Response<T>(200, "ok", null); }

    public static<T> Response<T> success(T data) { return  new Response<T>(200, "success", data); }

    public static<T> Response<T> error(CommonErr err) { return  new Response<T>(err.getCode(), err.getMessage(), null); }

    public static<T> Response<T> error(int code, String message) { return  new Response<T>(code, message, null); }
}
