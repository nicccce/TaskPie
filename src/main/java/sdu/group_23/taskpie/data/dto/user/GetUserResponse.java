package sdu.group_23.taskpie.data.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetUserResponse {
    private Integer userId;
    private String studentId;
    private String avatar_url;
    private String realName;
    private String nickName;
    private String bio;
    private Integer role;
    private LocalDateTime createdAt;
}
