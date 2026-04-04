package sdu.group_23.taskpie.data.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private Integer userId;
    private String role;
    private String token;
}
