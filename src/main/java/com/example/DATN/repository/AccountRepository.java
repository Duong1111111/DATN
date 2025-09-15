package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByUserIdAndStatus(Integer userId, AccountStatus status);
    List<Account> findByStatus(AccountStatus status);
    boolean existsByUsername(String username);
    List<Account> findByRole(Role role);
    Optional<Account> findByEmail(String email);

    @Query("SELECT a.role, COUNT(a) FROM Account a GROUP BY a.role")
    List<Object[]> countUsersByRole();

    // Thời gian duyệt trung bình (giờ)
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (updated_at - created_at))) / 3600 " +
            "FROM accounts " +
            "WHERE role = 'COMPANY' AND status = 'ACTIVE' AND created_at != updated_at",
            nativeQuery = true)
    Double getAvgCompanyApprovalTime();

    // Thống kê tăng trưởng user
    @Query(value = """
        SELECT DATE_TRUNC(:period, created_at) as period, COUNT(*)
        FROM Accounts
        GROUP BY period
        ORDER BY period
        """, nativeQuery = true)
    List<Object[]> countUserGrowth(@Param("period") String period);
}