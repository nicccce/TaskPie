package sdu.group_23.taskpie.data.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    // 系统->全体用户
    SYSTEM(10),
    // 系统->用户
    PERSONAL(11),

    // 小组->全体成员
    TEAM_ALL(20),
    // 小组->单个成员
    TEAM_ONE(21),

    // 小组<->用户
    INVITATION(30),
    APPLICATION(31);

    private final int value;

}
