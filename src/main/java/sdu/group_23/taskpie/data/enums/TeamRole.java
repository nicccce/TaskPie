package sdu.group_23.taskpie.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamRole {

    LEADER(10),
    MEMBER(1);

    public final int value;

}
