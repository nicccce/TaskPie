package sdu.group_23.taskpie.data.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InviteRequest {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;
}
