package sdu.group_23.taskpie.data.dto.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewTaskRequest {

    @NotNull(message = "审核结果不能为空")
    private Boolean passed;

    @Size(max = 500, message = "反馈长度不能超过500")
    private String feedback;
}
