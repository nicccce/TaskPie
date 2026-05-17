package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.user.*;
import sdu.group_23.taskpie.data.po.User;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.UserRepository;
import sdu.group_23.taskpie.util.UserContextUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Response<GetMeResponse> getMe() {
        User user = userRepository.findByUserId(UserContextUtil.getCurrentUserId());

        GetMeResponse response = GetMeResponse.builder()
                .userId(user.getUserId())
                .studentId(user.getStudentId())
                .userName(user.getUserName())
                .avatar_url(user.getAvatarUrl())
                .realName(user.getRealName())
                .nickName(user.getNickName())
                .bio(user.getBio())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return Response.success(response);
    }

    public Response<Void> updateMe(UpdateMeRequest request) {
        User user = userRepository.findByUserId(UserContextUtil.getCurrentUserId());
        if(!user.getStudentId().equals(request.getStudentId()) && userRepository.existsByStudentId(request.getStudentId())) {
            return Response.error(CommonErr.STUDENT_ID_ALREADY_EXISTS);
        }

        user.setStudentId(request.getStudentId());
        user.setRealName(request.getRealName());
        user.setNickName(request.getNickName());
        user.setBio(request.getBio());

        userRepository.save(user);
        return Response.ok();
    }

    public Response<GetUserResponse> getUser(Integer userId) {
        User user = userRepository.findByUserId(userId);
        if(user == null) { return Response.error(CommonErr.USER_NOT_FOUND); }

        GetUserResponse response = GetUserResponse.builder()
                .userId(user.getUserId())
                .studentId(user.getStudentId())
                .avatar_url(user.getAvatarUrl())
                .realName(user.getRealName())
                .nickName(user.getNickName())
                .bio(user.getBio())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return Response.success(response);
    }

    public Response<PageResponse<SelectUserResponse>> select(SelectUserRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<User> page = userRepository.selectByKeyword(request.getKeyword(), pageable);

        Page<SelectUserResponse> response = page.map(user -> SelectUserResponse.builder()
                .userId(user.getUserId())
                .studentId(user.getStudentId())
                .avatar_url(user.getAvatarUrl())
                .realName(user.getRealName())
                .nickName(user.getNickName())
                .build());

        return Response.success(PageResponse.pageToResponse(response));
    }

}

