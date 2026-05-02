package sdu.group_23.taskpie.data.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    // 系统->全体用户
    SYSTEM(10),

    // 系统->用户
    PERSONAL(20),

    // 小组->全体成员
    TEAM_ANNOUNCEMENT(30),
    TEAM_TASK(31),

    // 小组->用户
    INVITATION(40),
    APPLICATION(41);

    private final int value;

}
