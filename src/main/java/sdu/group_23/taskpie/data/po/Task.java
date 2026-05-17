package sdu.group_23.taskpie.data.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;

    @Column(nullable = false)
    private Integer teamId;

    @Column(nullable = false)
    private Integer creatorId;

    private Integer assigneeId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private Integer status;

    private LocalDateTime deadline;

    @Column(length = 1000)
    private String submitContent;

    @Column(length = 500)
    private String reviewFeedback;

    @Column(length = 255)
    private String attachmentName;

    @Column(length = 1000)
    private String attachmentPath;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
