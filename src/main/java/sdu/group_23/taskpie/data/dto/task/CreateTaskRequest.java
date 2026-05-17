package sdu.group_23.taskpie.data.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateTaskRequest {

    @NotNull(message = "小组ID不能为空")
    private Integer teamId;

    private Integer assigneeId;

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题长度不能超过100")
    private String title;

    @Size(max = 1000, message = "任务描述长度不能超过1000")
    private String description;

    @Min(value = 1, message = "优先级范围为1~4")
    @Max(value = 4, message = "优先级范围为1~4")
    private Integer priority = 2;

    private LocalDateTime deadline;
}
