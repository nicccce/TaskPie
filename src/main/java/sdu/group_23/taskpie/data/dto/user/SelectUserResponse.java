package sdu.group_23.taskpie.data.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectUserResponse {
    private Integer userId;
    private String studentId;
    private String avatar_url;
    private String realName;
    private String nickName;
}
