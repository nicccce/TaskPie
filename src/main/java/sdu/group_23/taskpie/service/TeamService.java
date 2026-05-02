package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.enums.TeamRole;
import sdu.group_23.taskpie.data.po.Team;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
import sdu.group_23.taskpie.repository.TeamRepository;
import sdu.group_23.taskpie.util.UserContextUtil;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Response<Integer> create(CreateRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .leaderId(UserContextUtil.getCurrentUserId())
                .build();

        teamRepository.save(team);

        TeamMember teamMember = TeamMember.builder()
                .teamId(team.getTeamId())
                .userId(team.getLeaderId())
                .role(TeamRole.LEADER.getValue())
                .build();

        teamMemberRepository.save(teamMember);
        return Response.success(team.getTeamId());
    }
}
