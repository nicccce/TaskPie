package sdu.group_23.taskpie.data.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SelectUserRequest {

    @NotBlank(message = "关键字不能为空")
    private String keyword;

    @Min(value = 0, message = "页数不能小于0")
    private int page = 0;

    @Min(value = 0, message = "页大小不能小于0")
    private int size = 10;
}
