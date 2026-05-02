package sdu.group_23.taskpie.data.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    private final int value;
}
