package sdu.group_23.taskpie.data.dto.team;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetTeamResponse {
    private Integer teamId;
    private String name;
    private String description;
    private Integer leaderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
