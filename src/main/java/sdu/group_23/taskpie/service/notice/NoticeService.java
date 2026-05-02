package sdu.group_23.taskpie.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.notice.GetNoticeResponse;
import sdu.group_23.taskpie.data.enums.notice.Type;
import sdu.group_23.taskpie.data.po.Notice;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.NoticeRepository;
import sdu.group_23.taskpie.util.PermissionChecker;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final PermissionChecker permissionChecker;

    public Response<GetNoticeResponse> getNotice(Integer noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId);

        if (notice == null ||
                (notice.getType() == Type.PERSONAL.getValue() && !notice.getReceiverId().equals(UserContextUtil.getCurrentUserId())) ||
                (notice.getType() == Type.TEAM_ANNOUNCEMENT.getValue() && !permissionChecker.isTeamMember(notice.getTeamId())) ||
                (notice.getType() == Type.TEAM_TASK.getValue() && permissionChecker.isTeamMember(notice.getTeamId()))

        ) {
            return Response.error(CommonErr.NOTICE_NOT_FOUND);
        }

        notice.setRead(true);
        notice.setReadAt(LocalDateTime.now());
        noticeRepository.save(notice);

        GetNoticeResponse response = GetNoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .type(notice.getType())
                .title(notice.getTitle())
                .content(notice.getContent())
                .senderId(notice.getSenderId())
                .teamId(notice.getTeamId())
                .receiverId(notice.getReceiverId())
                .status(notice.getStatus())
                .read(notice.getRead())
                .top(notice.getTop())
                .createdAt(notice.getCreatedAt())
                .readAt(notice.getReadAt())
                .build();

        return Response.success(response);

    }

}