package sdu.group_23.taskpie.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sdu.group_23.taskpie.data.po.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
}
