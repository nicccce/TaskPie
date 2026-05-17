package sdu.group_23.taskpie.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.group_23.taskpie.data.po.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Task findByTaskId(Integer taskId);

    @Query("SELECT t FROM Task t WHERE t.teamId = :teamId AND (:status IS NULL OR t.status = :status) ORDER BY t.createdAt DESC")
    Page<Task> findTeamTasks(@Param("teamId") Integer teamId, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.teamId IN :teamIds AND (:status IS NULL OR t.status = :status) ORDER BY t.createdAt DESC")
    Page<Task> findJoinedTeamTasks(@Param("teamIds") List<Integer> teamIds, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assigneeId = :userId AND (:status IS NULL OR t.status = :status) ORDER BY t.createdAt DESC")
    Page<Task> findAssignedTasks(@Param("userId") Integer userId, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assigneeId = :userId AND t.teamId IN :teamIds AND (:status IS NULL OR t.status = :status) ORDER BY t.createdAt DESC")
    Page<Task> findAssignedTasksInTeams(@Param("userId") Integer userId, @Param("teamIds") List<Integer> teamIds, @Param("status") Integer status, Pageable pageable);
}
