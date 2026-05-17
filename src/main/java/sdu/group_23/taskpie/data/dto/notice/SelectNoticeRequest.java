package sdu.group_23.taskpie.data.dto.notice;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SelectNoticeRequest {

    @Min(value = 0, message = "页数不能小于0")
    private int page = 0;

    @Min(value = 0, message = "页大小不能小于0")
    private int size = 10;

    private Integer type;//0-全部；1-系统；2-小组；3-邀请/申请
    private Integer teamId;
}
