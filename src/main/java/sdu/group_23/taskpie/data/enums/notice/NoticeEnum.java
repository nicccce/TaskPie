package sdu.group_23.taskpie.data.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeEnum {

    //system

    //personal
    PERSONAL_HELLO(Type.PERSONAL, "欢迎加入任务派！", "感谢您注册任务派，开始高效管理您的任务吧！"),

    //team_all
    TEAM_ALL(Type.TEAM_ALL, null, null),

    //team_one

    //invitation,application
    INVITATION(Type.INVITATION, "团队邀请", "【%s】邀请您加入团队「%s」"),
    APPLICATION(Type.APPLICATION, "团队申请", "【%s】申请加入您的团队「%s」\n%s");

    private final Type type;
    private final String title;
    private final String content;

    public String formatContent(Object... args) {
        if (args == null || args.length == 0) {
            return this.content;
        }
        return String.format(this.content, args);
    }

}
