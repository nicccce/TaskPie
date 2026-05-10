package sdu.group_23.taskpie.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.group_23.taskpie.data.po.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Notice findByNoticeId(Integer noticeId);

    @Query("SELECT n FROM Notice n WHERE n.type IN :types AND (n.receiverId = :receiverId OR n.receiverId = 0) ORDER BY n.createdAt DESC")
    Page<Notice> findSystemNotices(@Param("types") List<Integer> types, @Param("receiverId") Integer receiverId, Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE n.type IN :types AND n.teamId IN :teamIds AND (n.receiverId = :receiverId OR n.receiverId = 0) ORDER BY n.createdAt DESC")
    Page<Notice> findTeamNotices(@Param("types") List<Integer> types, @Param("teamIds") List<Integer> teamIds, @Param("receiverId") Integer receiverId, Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE n.type IN :types AND n.receiverId = :receiverId ORDER BY n.createdAt DESC")
    Page<Notice> findInAndApNotices(@Param("types") List<Integer> types, @Param("receiverId") Integer receiverId, Pageable pageable);
}
