package sdu.group_23.taskpie.data.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String nickName;
    private Integer role;
    private String token;
}
