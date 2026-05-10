package sdu.group_23.taskpie.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.notice.GetNoticeResponse;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeRequest;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeResponse;
import sdu.group_23.taskpie.data.dto.team.GetTeamResponse;
import sdu.group_23.taskpie.data.enums.notice.Type;
import sdu.group_23.taskpie.data.po.Notice;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.NoticeRepository;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
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

        Page<Notice> noticePage;

        if (request.getType() == 1) {

            List<Integer> Types = List.of(Type.SYSTEM.getValue(), Type.PERSONAL.getValue());
            noticePage = noticeRepository.findSystemNotices(Types, userId, pageable);

        } else if (request.getType() == 2) {

            List<Integer> Types = List.of(Type.TEAM_ALL.getValue(), Type.TEAM_ONE.getValue());
            List<Integer> teamIds;

            if(request.getTeamId() != 0){
                if (!permissionChecker.isTeamMember(request.getTeamId())) {
                    return Response.error(CommonErr.NOT_TEAM_MEMBER);
                }
                teamIds = List.of(request.getTeamId());
            }else{
                teamIds = teamMemberRepository.findByUserId(userId)
                        .stream()
                        .map(TeamMember::getTeamId)
                        .collect(Collectors.toList());
            }

            noticePage = noticeRepository.findTeamNotices(Types, teamIds, userId, pageable);

        } else if (request.getType() == 3) {

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

}