package sdu.group_23.taskpie.util;

import org.springframework.stereotype.Component;

@Component
public class UserContextUtil {

    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void setCurrentUserId(Integer userId) {
        currentUserId.set(userId);
    }
    public static void setCurrentRole(String role){
        currentRole.set(role);
    }

    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }
    public static String getCurrentRole() {
        return currentRole.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentRole.remove();
    }

}
