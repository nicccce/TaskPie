package sdu.group_23.taskpie.data.dto.task;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Integer taskId;
    private Integer teamId;
    private Integer creatorId;
    private Integer assigneeId;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private LocalDateTime deadline;
    private String submitContent;
    private String reviewFeedback;
    private String attachmentName;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
