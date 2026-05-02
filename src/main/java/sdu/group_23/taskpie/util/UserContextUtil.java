package sdu.group_23.taskpie.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContextUtil {

    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<Integer> currentRole = new ThreadLocal<>();

    public static void setCurrentUserId(Integer userId) {
        currentUserId.set(userId);
    }
    public static void setCurrentRole(Integer role){
        currentRole.set(role);
    }

    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }
    public static Integer getCurrentRole() {
        return currentRole.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentRole.remove();
    }

}
