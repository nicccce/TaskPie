package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sdu.group_23.taskpie.data.dto.auth.LoginRequest;
import sdu.group_23.taskpie.data.dto.auth.LoginResponse;
import sdu.group_23.taskpie.data.dto.auth.PasswordRequest;
import sdu.group_23.taskpie.data.dto.auth.RegisterRequest;
import sdu.group_23.taskpie.data.enums.Role;
import sdu.group_23.taskpie.data.enums.notice.NoticeEnum;
import sdu.group_23.taskpie.data.po.User;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.UserRepository;
import sdu.group_23.taskpie.service.notice.NoticeCreate;
import sdu.group_23.taskpie.service.notice.NoticeService;
import sdu.group_23.taskpie.util.JwtUtil;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final NoticeCreate noticeCreate;

    public Response<Void> register(RegisterRequest request) {

        if (userRepository.existsByUserName(request.getUserName())) { return Response.error(CommonErr.USERNAME_ALREADY_EXISTS); }
        if (userRepository.existsByStudentId(request.getStudentId())) { return Response.error(CommonErr.STUDENT_ID_ALREADY_EXISTS); }

        User user = User.builder()
                .studentId(request.getStudentId())
                .userName(request.getUserName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName())
                .nickName(request.getNickName())
                .role(Role.MEMBER.getValue())
                .active(true)
                .build();

        userRepository.save(user);

        try {
            noticeCreate.personal(NoticeEnum.PERSONAL_HELLO, user.getUserId());
        } catch (Exception ignored) {}

        return Response.ok();
    }

    public Response<LoginResponse> login(LoginRequest request) {

        if (!userRepository.existsByUserName(request.getUserName())) { return Response.error(CommonErr.LOGIN_FAILURE); }

        User user = userRepository.findByUserName(request.getUserName());
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) { return Response.error(CommonErr.LOGIN_FAILURE); }
        if(!user.isActive()) { return Response.error(CommonErr.LOGIN_FAILURE); }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setNickName(user.getNickName());
        loginResponse.setRole(user.getRole());
        loginResponse.setToken(jwtUtil.generateToken(user.getUserId(),  user.getRole()));

        return Response.success(loginResponse);
    }

    public Response<Void> password(PasswordRequest request) {

        User user = userRepository.findByUserId(UserContextUtil.getCurrentUserId());

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) { return Response.error(CommonErr.PASSWORD_WRONG); }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return Response.ok();

    }

    public Response<Void> close(String password) {

        User user = userRepository.findByUserId(UserContextUtil.getCurrentUserId());

        if (!passwordEncoder.matches(password, user.getPasswordHash())) { return Response.error(CommonErr.PASSWORD_WRONG); }

        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        return Response.ok();
    }

}
