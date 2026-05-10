package sdu.group_23.taskpie.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sdu.group_23.taskpie.data.po.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    boolean existsByTeamIdAndUserId(Integer teamId, Integer userId);

    List<TeamMember> findByUserId(Integer userId);

    boolean existsByTeamIdAndUserIdAndRole(Integer teamId, Integer userId, Integer role);

    void deleteByTeamId(Integer teamId);

}