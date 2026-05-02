package sdu.group_23.taskpie.data.po;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sdu.group_23.taskpie.data.po.id.TeamMemberId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_member")
@IdClass(TeamMemberId.class)
public class TeamMember {

    @Id
    private Integer teamId;

    @Id
    private Integer userId;

    @Column(nullable = false)
    private Integer role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
