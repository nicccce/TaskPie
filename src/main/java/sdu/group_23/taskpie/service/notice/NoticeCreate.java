package sdu.group_23.taskpie.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.enums.notice.NoticeEnum;
import sdu.group_23.taskpie.data.enums.notice.Status;
import sdu.group_23.taskpie.data.po.Notice;
import sdu.group_23.taskpie.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeCreate {

    private final NoticeRepository noticeRepository;

    public void system(NoticeEnum noticeEnum){

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(-1)
                .teamId(-1)
                .receiverId(0)
                .status(0)
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);

    }

    public void personal(NoticeEnum noticeEnum, Integer receiverId){

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(-1)
                .teamId(-1)
                .receiverId(receiverId)
                .status(0)
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);

    }

    public void teamAnnouncement(NoticeEnum noticeEnum, Integer leaderId, Integer teamId){

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(leaderId)
                .teamId(teamId)
                .receiverId(0)
                .status(0)
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);

    }

    public void teamTask(NoticeEnum noticeEnum, Integer leaderId, Integer teamId, Integer receiverId){

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(leaderId)
                .teamId(teamId)
                .receiverId(receiverId)
                .status(0)
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);

    }

    public void invitation(Integer senderId, Integer teamId, Integer receiverId){

        NoticeEnum noticeEnum = NoticeEnum.INVITATION;

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(senderId)
                .teamId(teamId)
                .receiverId(receiverId)
                .status(Status.PENDING.getValue())
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);

    }

    public void application(Integer senderId, Integer teamId, Integer receiverId){

        NoticeEnum noticeEnum = NoticeEnum.APPLICATION;

        Notice notice = Notice.builder()
                .type(noticeEnum.getType().getValue())
                .title(noticeEnum.getTitle())
                .content(noticeEnum.getContent())
                .senderId(senderId)
                .teamId(teamId)
                .receiverId(receiverId)
                .status(Status.PENDING.getValue())
                .hasRead(false)
                .top(false)
                .build();

        noticeRepository.save(notice);
    }
}
