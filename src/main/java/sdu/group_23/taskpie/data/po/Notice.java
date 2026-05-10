package sdu.group_23.taskpie.data.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    /*
    系统通知对receiverId(0:全体)有效
    小组对senderId teamId receiverId(0:全体)有效
    邀请、申请对senderId teamId receiverId有效

    无效位置为负(正常情况)
     */

    @Column(nullable = false)
    private Integer senderId;

    @Column(nullable = false)
    private Integer teamId;

    @Column(nullable = false)
    private Integer receiverId;

    private Integer status;

    @Column(nullable = false)
    private Boolean hasRead;

    @Column(nullable = false)
    private Boolean top;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
