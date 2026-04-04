package sdu.group_23.taskpie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.group_23.taskpie.data.po.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
