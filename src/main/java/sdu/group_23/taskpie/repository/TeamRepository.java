package sdu.group_23.taskpie.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.group_23.taskpie.data.po.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findByTeamId(Integer teamId);

    void deleteByTeamId(Integer teamId);

    @Query("SELECT t FROM Team t WHERE t.name LIKE %:keyword%")
    Page<Team> selectByNameKeyword(@Param("keyword") String keyword, Pageable pageable);

}
