package sdu.group_23.taskpie.data.dto.team;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeamMemberResponse {
    private Integer userId;
    private String studentId;
    private String realName;
    private String nickName;
    private String avatar_url;
    private Integer role;
    private LocalDateTime joinedAt;
}
