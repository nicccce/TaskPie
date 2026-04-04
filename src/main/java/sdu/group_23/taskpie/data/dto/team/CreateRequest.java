package sdu.group_23.taskpie.data.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateRequest {

    @NotBlank(message = "小组名不能为空")
    @Size(max = 20, message = "小组名长度不能超过20")
    private String name;

    @Size(max = 500, message = "小组描述长度不能超过500")
    private String description;
}
