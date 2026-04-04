package sdu.group_23.taskpie.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sdu.group_23.taskpie.data.dto.auth.LoginRequest;
import sdu.group_23.taskpie.data.dto.auth.LoginResponse;
import sdu.group_23.taskpie.data.dto.auth.PasswordRequest;
import sdu.group_23.taskpie.data.dto.auth.RegisterRequest;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.AuthService;
import sdu.group_23.taskpie.util.JwtUtil;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Response<Void> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PatchMapping("/password")
    public Response<Void> password(@RequestBody @Valid PasswordRequest passwordRequest) {
        return authService.password(passwordRequest);
    }

    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request) {
        try{
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtUtil.invalidateToken(token);
            }
            return Response.ok();
        } catch(Exception e){
            return Response.ok();
        }
    }

    @PostMapping("/close")
    public Response<Void> close(@RequestParam String password) {
        return authService.close(password);
    }

}