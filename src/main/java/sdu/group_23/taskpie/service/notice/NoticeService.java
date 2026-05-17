package sdu.group_23.taskpie.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.notice.HandleNoticeRequest;
import sdu.group_23.taskpie.data.dto.notice.GetNoticeResponse;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeRequest;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeResponse;
import sdu.group_23.taskpie.data.enums.TeamRole;
import sdu.group_23.taskpie.data.enums.notice.Status;
import sdu.group_23.taskpie.data.enums.notice.Type;
import sdu.group_23.taskpie.data.po.Notice;
import sdu.group_23.taskpie.data.po.Team;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.NoticeRepository;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
import sdu.group_23.taskpie.repository.TeamRepository;
import sdu.group_23.taskpie.util.PermissionChecker;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final PermissionChecker permissionChecker;

    public Response<GetNoticeResponse> getNotice(Integer noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId);

        if (notice == null ||
                (notice.getType() == Type.PERSONAL.getValue() && !notice.getReceiverId().equals(UserContextUtil.getCurrentUserId())) ||
                (notice.getType() == Type.TEAM_ALL.getValue() && !permissionChecker.isTeamMember(notice.getTeamId())) ||
                (notice.getType() == Type.TEAM_ONE.getValue() && (!permissionChecker.isTeamMember(notice.getTeamId()) || !notice.getReceiverId().equals(UserContextUtil.getCurrentUserId()))) ||
                (notice.getType() == Type.INVITATION.getValue() && !notice.getReceiverId().equals(UserContextUtil.getCurrentUserId()))||
                (notice.getType() == Type.APPLICATION.getValue() && !notice.getReceiverId().equals(UserContextUtil.getCurrentUserId()))
        ) {
            return Response.error(CommonErr.NOTICE_NOT_FOUND);
        }

        if(notice.getHasRead().equals(false)){
            notice.setHasRead(true);
            notice.setReadAt(LocalDateTime.now());
            noticeRepository.save(notice);
        }

        GetNoticeResponse response = GetNoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .type(notice.getType())
                .title(notice.getTitle())
                .content(notice.getContent())
                .senderId(notice.getSenderId())
                .teamId(notice.getTeamId())
                .receiverId(notice.getReceiverId())
                .status(notice.getStatus())
                .hasRead(notice.getHasRead())
                .top(notice.getTop())
                .createdAt(notice.getCreatedAt())
                .readAt(notice.getReadAt())
                .build();

        return Response.success(response);

    }

    public Response<PageResponse<SelectNoticeResponse>> select(SelectNoticeRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Integer userId = UserContextUtil.getCurrentUserId();
        Integer type = request.getType() == null ? 0 : request.getType();
        Integer teamId = request.getTeamId() == null ? 0 : request.getTeamId();

        Page<Notice> noticePage;

        if (type == 0) {

            List<Integer> systemTypes = List.of(Type.SYSTEM.getValue(), Type.PERSONAL.getValue());
            List<Integer> teamTypes = List.of(Type.TEAM_ALL.getValue(), Type.TEAM_ONE.getValue());
            List<Integer> actionTypes = List.of(Type.INVITATION.getValue(), Type.APPLICATION.getValue());
            List<Integer> teamIds = teamMemberRepository.findByUserId(userId)
                    .stream()
                    .map(TeamMember::getTeamId)
                    .collect(Collectors.toList());
            if (teamIds.isEmpty()) {
                teamIds = List.of(-1);
            }
            noticePage = noticeRepository.findVisibleNotices(systemTypes, teamTypes, actionTypes, teamIds, userId, pageable);

        } else if (type == 1) {

            List<Integer> Types = List.of(Type.SYSTEM.getValue(), Type.PERSONAL.getValue());
            noticePage = noticeRepository.findSystemNotices(Types, userId, pageable);

        } else if (type == 2) {

            List<Integer> Types = List.of(Type.TEAM_ALL.getValue(), Type.TEAM_ONE.getValue());
            List<Integer> teamIds;

            if(teamId != 0){
                if (!permissionChecker.isTeamMember(teamId)) {
                    return Response.error(CommonErr.NOT_TEAM_MEMBER);
                }
                teamIds = List.of(teamId);
            }else{
                teamIds = teamMemberRepository.findByUserId(userId)
                        .stream()
                        .map(TeamMember::getTeamId)
                        .collect(Collectors.toList());
                if (teamIds.isEmpty()) {
                    teamIds = List.of(-1);
                }
            }

            noticePage = noticeRepository.findTeamNotices(Types, teamIds, userId, pageable);

        } else if (type == 3) {

            List<Integer> Types = List.of(Type.INVITATION.getValue(), Type.APPLICATION.getValue());
            noticePage = noticeRepository.findInAndApNotices(Types, userId, pageable);

        } else {
            return Response.error(CommonErr.PARAM_ERROR);
        }

        Page<SelectNoticeResponse> response = noticePage.map( notice ->SelectNoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .type(notice.getType())
                .title(notice.getTitle())
                .senderId(notice.getSenderId())
                .teamId(notice.getTeamId())
                .receiverId(notice.getReceiverId())
                .status(notice.getStatus())
                .hasRead(notice.getHasRead())
                .top(notice.getTop())
                .createdAt(notice.getCreatedAt())
                .readAt(notice.getReadAt())
                .build());

        return Response.success(PageResponse.pageToResponse(response));
    }

    @Transactional
    public Response<Void> handleNotice(Integer noticeId, HandleNoticeRequest request) {
        Notice notice = noticeRepository.findByNoticeId(noticeId);
        if (notice == null) {
            return Response.error(CommonErr.NOTICE_NOT_FOUND);
        }

        Integer currentUserId = UserContextUtil.getCurrentUserId();
        boolean isInvitation = notice.getType().equals(Type.INVITATION.getValue());
        boolean isApplication = notice.getType().equals(Type.APPLICATION.getValue());
        if (!isInvitation && !isApplication) {
            return Response.error(CommonErr.PARAM_ERROR);
        }
        if (!notice.getReceiverId().equals(currentUserId)) {
            return Response.error(CommonErr.NOTICE_NOT_FOUND);
        }
        if (!notice.getStatus().equals(Status.PENDING.getValue())) {
            return Response.error(CommonErr.NOTICE_STATUS_INVALID);
        }

        Team team = teamRepository.findByTeamId(notice.getTeamId());
        if (team == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }

        if (Boolean.TRUE.equals(request.getAccept())) {
            Integer targetUserId = isInvitation ? notice.getReceiverId() : notice.getSenderId();
            if (!teamMemberRepository.existsByTeamIdAndUserId(notice.getTeamId(), targetUserId)) {
                TeamMember member = TeamMember.builder()
                        .teamId(notice.getTeamId())
                        .userId(targetUserId)
                        .role(TeamRole.MEMBER.getValue())
                        .build();
                teamMemberRepository.save(member);
            }
            notice.setStatus(Status.ACCEPTED.getValue());
        } else {
            notice.setStatus(Status.REJECTED.getValue());
        }

        notice.setHasRead(true);
        notice.setReadAt(LocalDateTime.now());
        noticeRepository.save(notice);
        return Response.ok();
    }

}
