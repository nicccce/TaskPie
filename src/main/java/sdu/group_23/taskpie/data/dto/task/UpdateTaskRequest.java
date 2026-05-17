package sdu.group_23.taskpie.data.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateTaskRequest {

    private Integer assigneeId;

    @Size(max = 100, message = "任务标题长度不能超过100")
    private String title;

    @Size(max = 1000, message = "任务描述长度不能超过1000")
    private String description;

    @Min(value = 1, message = "优先级范围为1~4")
    @Max(value = 4, message = "优先级范围为1~4")
    private Integer priority;

    @Min(value = 1, message = "任务状态范围为1~5")
    @Max(value = 5, message = "任务状态范围为1~5")
    private Integer status;

    private LocalDateTime deadline;
}
