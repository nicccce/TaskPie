package sdu.group_23.taskpie.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import sdu.group_23.taskpie.data.po.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String username);
    boolean existsByStudentId(String studentId);
    User findByUserName(String username);
    User findByUserId(Integer userId);

    @Query("SELECT u FROM User u WHERE u.studentId = :keyword OR u.realName LIKE %:keyword% OR u.nickName LIKE %:keyword%")
    Page<User> selectByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
