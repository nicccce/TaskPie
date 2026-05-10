package sdu.group_23.taskpie.data.dto.team;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMyTeamResponse {

    private List<TeamInfo> teams;

    @Data
    @Builder
    public static class TeamInfo {
        private Integer teamId;
        private String teamName;
    }
}
