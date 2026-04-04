package sdu.group_23.taskpie.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sdu.group_23.taskpie.data.po.User;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.repository.UserRepository;
import sdu.group_23.taskpie.util.JwtUtil;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            writeJsonResponse(response, Response.error(CommonErr.UNAUTHORIZED));
            return false;
        }

        token = token.substring(7);

        if (!jwtUtil.validateToken(token)) {
            writeJsonResponse(response, Response.error(CommonErr.UNAUTHORIZED));
            return false;
        }

        try{
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            User user = userRepository.findByUserId(userId);

            if (user == null || !user.isActive() ) { writeJsonResponse(response, Response.error(CommonErr.AUTH_EXCEPTION)); return false; }
            else { UserContextUtil.setCurrentUserId(userId); UserContextUtil.setCurrentRole(role); }

        } catch (Exception e){
            writeJsonResponse(response, Response.error(CommonErr.UNAUTHORIZED));
            return false;
        }
        return true;
    }

    private void writeJsonResponse(HttpServletResponse response, Response<?> responseVO) throws IOException {
        response.setStatus(responseVO.getCode());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseVO));
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler, Exception ex) throws Exception {
        UserContextUtil.clear();
    }
}