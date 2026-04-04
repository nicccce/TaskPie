package sdu.group_23.taskpie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.user.*;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Response<GetMeResponse> getMe() {
        return userService.getMe();
    }

    @PutMapping("/me")
    public Response<Void> updateMe(@RequestBody @Valid UpdateMeRequest updateMeRequest) {
        return userService.updateMe(updateMeRequest);
    }

    @GetMapping("/{userId}")
    public Response<GetUserResponse> getUser(@PathVariable Integer userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/select")
    public Response<PageResponse<SelectUserResponse>> select(@RequestBody @Valid SelectUserRequest selectUserRequest) {
        return userService.select(selectUserRequest);
    }
}
