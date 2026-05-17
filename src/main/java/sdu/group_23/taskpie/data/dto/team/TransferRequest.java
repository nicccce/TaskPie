package sdu.group_23.taskpie.data.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TransferRequest {

    @NotNull(message = "新组长ID不能为空")
    private Integer userId;
}
