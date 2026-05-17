package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.dto.team.GetMyTeamResponse;
import sdu.group_23.taskpie.data.dto.team.GetTeamResponse;
import sdu.group_23.taskpie.data.dto.team.TeamMemberResponse;
import sdu.group_23.taskpie.data.dto.team.SelectTeamRequest;
import sdu.group_23.taskpie.data.enums.TeamRole;
import sdu.group_23.taskpie.data.po.Team;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.po.User;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
import sdu.group_23.taskpie.repository.TeamRepository;
import sdu.group_23.taskpie.repository.UserRepository;
import sdu.group_23.taskpie.service.notice.NoticeCreate;
import sdu.group_23.taskpie.util.PermissionChecker;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;
    private final NoticeCreate noticeCreate;

    @Transactional
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

    @Transactional
    public Response<Void> delete(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }

        if(!permissionChecker.isTeamLeader(teamId)) {
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

    public Response<List<TeamMemberResponse>> getMembers(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        if (!permissionChecker.isTeamMember(teamId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        List<Integer> userIds = members.stream().map(TeamMember::getUserId).toList();
        Map<Integer, User> userMap = userRepository.findAllByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        List<TeamMemberResponse> response = members.stream()
                .map(member -> {
                    User user = userMap.get(member.getUserId());
                    return TeamMemberResponse.builder()
                            .userId(member.getUserId())
                            .studentId(user == null ? null : user.getStudentId())
                            .realName(user == null ? null : user.getRealName())
                            .nickName(user == null ? null : user.getNickName())
                            .avatar_url(user == null ? null : user.getAvatarUrl())
                            .role(member.getRole())
                            .joinedAt(member.getJoinedAt())
                            .build();
                })
                .toList();

        return Response.success(response);
    }

    public Response<Void> apply(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, currentUserId)) {
            return Response.error(CommonErr.TEAM_MEMBER_ALREADY_EXISTS);
        }

        noticeCreate.application(currentUserId, teamId, team.getLeaderId());
        return Response.ok();
    }

    public Response<Void> invite(Integer teamId, Integer userId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(teamId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (!userRepository.existsByUserId(userId)) {
            return Response.error(CommonErr.USER_NOT_FOUND);
        }
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            return Response.error(CommonErr.TEAM_MEMBER_ALREADY_EXISTS);
        }

        noticeCreate.invitation(UserContextUtil.getCurrentUserId(), teamId, userId);
        return Response.ok();
    }

    @Transactional
    public Response<Void> removeMembers(Integer teamId, List<Integer> userIds) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(teamId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (userIds == null || userIds.isEmpty() || userIds.contains(team.getLeaderId())) {
            return Response.error(CommonErr.PARAM_ERROR);
        }

        for (Integer userId : userIds) {
            teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
        }
        return Response.ok();
    }

    @Transactional
    public Response<Void> quit(Integer teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, currentUserId)) {
            return Response.error(CommonErr.NOT_TEAM_MEMBER);
        }
        if (team.getLeaderId().equals(currentUserId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }

        teamMemberRepository.deleteByTeamIdAndUserId(teamId, currentUserId);
        return Response.ok();
    }

    @Transactional
    public Response<Void> transfer(Integer teamId, Integer newLeaderId) {
        Team team = teamRepository.findByTeamId(teamId);
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(teamId)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        TeamMember newLeader = teamMemberRepository.findByTeamIdAndUserId(teamId, newLeaderId);
        if (newLeader == null) {
            return Response.error(CommonErr.NOT_TEAM_MEMBER);
        }

        TeamMember oldLeader = teamMemberRepository.findByTeamIdAndUserId(teamId, UserContextUtil.getCurrentUserId());
        oldLeader.setRole(TeamRole.MEMBER.getValue());
        newLeader.setRole(TeamRole.LEADER.getValue());
        team.setLeaderId(newLeaderId);

        teamRepository.save(team);
        teamMemberRepository.save(oldLeader);
        teamMemberRepository.save(newLeader);
        return Response.ok();
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
