package sdu.group_23.taskpie.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.po.id.TeamMemberId;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    boolean existsByTeamIdAndUserId(Integer teamId, Integer userId);

    List<TeamMember> findByUserId(Integer userId);

    List<TeamMember> findByTeamId(Integer teamId);

    TeamMember findByTeamIdAndUserId(Integer teamId, Integer userId);

    boolean existsByTeamIdAndUserIdAndRole(Integer teamId, Integer userId, Integer role);

    void deleteByTeamId(Integer teamId);

    void deleteByTeamIdAndUserId(Integer teamId, Integer userId);

}
