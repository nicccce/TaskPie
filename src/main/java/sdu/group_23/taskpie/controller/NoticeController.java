package sdu.group_23.taskpie.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.notice.HandleNoticeRequest;
import sdu.group_23.taskpie.data.dto.notice.GetNoticeResponse;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeRequest;
import sdu.group_23.taskpie.data.dto.notice.SelectNoticeResponse;
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

    @PostMapping("/select")
    public Response<PageResponse<SelectNoticeResponse>> select(@RequestBody @Valid SelectNoticeRequest selectNoticeRequest) {
        return noticeService.select(selectNoticeRequest);
    }

    @PatchMapping("/{noticeId}/handle")
    public Response<Void> handleNotice(@PathVariable Integer noticeId, @RequestBody @Valid HandleNoticeRequest request) {
        return noticeService.handleNotice(noticeId, request);
    }

}
