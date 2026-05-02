package sdu.group_23.taskpie.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sdu.group_23.taskpie.repository.TeamMemberRepository;

@Component
@RequiredArgsConstructor
public class PermissionChecker {

    private final TeamMemberRepository teamMemberRepository;

    public boolean isTeamMember(Integer teamId) {
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, UserContextUtil.getCurrentUserId());
    }


}
