package sdu.group_23.taskpie.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sdu.group_23.taskpie.data.enums.TeamRole;
import sdu.group_23.taskpie.repository.TeamMemberRepository;

@Component
@RequiredArgsConstructor
public class PermissionChecker {

    private final TeamMemberRepository teamMemberRepository;

    public boolean isTeamMember(Integer teamId) {
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, UserContextUtil.getCurrentUserId());
    }

    public boolean isTeamLeader(Integer teamId) {
        return teamMemberRepository.existsByTeamIdAndUserIdAndRole(
                teamId,
                UserContextUtil.getCurrentUserId(),
                TeamRole.LEADER.getValue()
        );
    }

}
