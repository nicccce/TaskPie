package sdu.group_23.taskpie.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING(1),
    IN_PROGRESS(2),
    SUBMITTED(3),
    COMPLETED(4),
    REJECTED(5);

    private final int value;

    public static boolean contains(Integer value) {
        if (value == null) {
            return false;
        }
        for (TaskStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}
