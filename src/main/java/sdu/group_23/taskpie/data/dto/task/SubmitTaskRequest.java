package sdu.group_23.taskpie.data.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SubmitTaskRequest {

    @NotBlank(message = "提交内容不能为空")
    @Size(max = 1000, message = "提交内容长度不能超过1000")
    private String content;
}
