package sdu.group_23.taskpie.data.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMeRequest {

    @NotBlank(message = "学号不能为空")
    @Size(min = 8, max = 20, message = "学号长度须在 8~20 个字符之内")
    @Pattern(regexp = "^[0-9]+$", message = "学号只能包含数字")
    private String studentId;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 20, message = "真实姓名长度须在 2~20 个字符之内")
    private String realName;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度须在 2~20 个字符之内")
    private String nickName;

    @Size(max = 100, message = "简介长度不能大于200个字符")
    private String bio;
}
