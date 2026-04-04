package sdu.group_23.taskpie.data.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "学号不能为空")
    @Size(min = 8, max = 20, message = "学号长度须在 8~20 个字符之内")
    @Pattern(regexp = "^[0-9]+$", message = "学号只能包含数字")
    private String studentId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度须在 2~20 个字符之内")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线或汉字")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度须在 6~32 个字符之内")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 20, message = "真实姓名长度须在 2~20 个字符之内")
    private String realName;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度须在 2~20 个字符之内")
    private String nickName;
}
