package sdu.group_23.taskpie.data.dto.notice;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SelectNoticeResponse {
    private Integer noticeId;
    private Integer type;
    private String title;
    private Integer senderId;
    private Integer teamId;
    private Integer receiverId;
    private Integer status;
    private Boolean hasRead;
    private Boolean top;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
