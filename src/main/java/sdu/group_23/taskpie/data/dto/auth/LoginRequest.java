package sdu.group_23.taskpie.data.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private  String userName;

    @NotBlank(message = "密码不能为空")
    private String password;
}
