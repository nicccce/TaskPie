package sdu.group_23.taskpie.data.dto.notice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class HandleNoticeRequest {

    @NotNull(message = "处理结果不能为空")
    private Boolean accept;

    @Size(max = 200, message = "反馈长度不能超过200")
    private String feedback;
}
