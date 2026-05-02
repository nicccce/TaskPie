package sdu.group_23.taskpie.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sdu.group_23.taskpie.data.dto.notice.GetNoticeResponse;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.notice.NoticeService;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{noticeId}")
    public Response<GetNoticeResponse> getNotice(@PathVariable Integer noticeId) {
        return noticeService.getNotice(noticeId);
    }

}
