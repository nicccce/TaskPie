package sdu.group_23.taskpie.data.dto.team;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class UserIdsRequest {

    @NotEmpty(message = "用户ID列表不能为空")
    private List<Integer> userIds;
}
