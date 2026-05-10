package sdu.group_23.taskpie.data.dto.team;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SelectTeamRequest {

    @NotBlank(message = "关键字不能为空")
    private String keyword;

    @Min(value = 0, message = "页数不能小于0")
    private Integer page = 0;

    @Min(value = 0, message = "页大小不能小于0")
    private Integer size = 10;

}
