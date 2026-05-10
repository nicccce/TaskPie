package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.dto.team.GetMyTeamResponse;
import sdu.group_23.taskpie.data.dto.team.GetTeamResponse;
import sdu.group_23.taskpie.data.dto.team.SelectTeamRequest;
import sdu.group_23.taskpie.data.enums.TeamRole;
import sdu.group_23.taskpie.data.po.Team;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
import sdu.group_23.taskpie.repository.TeamRepository;
import sdu.group_23.taskpie.util.PermissionChecker;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PermissionChecker permissionChecker;

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

    public Response<Void> delete(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }

        if(!permissionChecker.isTeamMember(teamId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }

        teamRepository.deleteByTeamId(teamId);
        teamMemberRepository.deleteByTeamId(teamId);

        return Response.ok();
    }

    public Response<GetTeamResponse> getTeam(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        GetTeamResponse response = GetTeamResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .description(team.getDescription())
                .leaderId(team.getLeaderId())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();

        return Response.success(response);
    }

    public Response<GetMyTeamResponse> getMyTeam() {
        Integer currentUserId = UserContextUtil.getCurrentUserId();

        List<TeamMember> teamMembers = teamMemberRepository.findByUserId(currentUserId);

        List<GetMyTeamResponse.TeamInfo> teams = teamMembers.stream()
                .map(teamMember -> {
                    Team team = teamRepository.findByTeamId(teamMember.getTeamId());
                    return GetMyTeamResponse.TeamInfo.builder()
                            .teamId(teamMember.getTeamId())
                            .teamName(team != null ? team.getName() : "未知团队")
                            .build();
                })
                .collect(Collectors.toList());

        GetMyTeamResponse response = GetMyTeamResponse.builder()
                .teams(teams)
                .build();

        return Response.success(response);
    }

    public Response<PageResponse<GetTeamResponse>> select(SelectTeamRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Team> page = teamRepository.selectByNameKeyword(request.getKeyword(), pageable);

        Page<GetTeamResponse> response = page.map(team -> GetTeamResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .description(team.getDescription())
                .leaderId(team.getLeaderId())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build());

        return Response.success(PageResponse.pageToResponse(response));
    }

}
