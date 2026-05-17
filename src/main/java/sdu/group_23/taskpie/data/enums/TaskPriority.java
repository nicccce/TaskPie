package sdu.group_23.taskpie.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskPriority {

    LOW(1),
    NORMAL(2),
    HIGH(3),
    URGENT(4);

    private final int value;

    public static boolean contains(Integer value) {
        if (value == null) {
            return false;
        }
        for (TaskPriority priority : values()) {
            if (priority.value == value) {
                return true;
            }
        }
        return false;
    }
}
