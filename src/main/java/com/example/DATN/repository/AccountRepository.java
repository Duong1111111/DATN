package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByUserIdAndStatus(Integer userId, AccountStatus status);
    List<Account> findByStatus(AccountStatus status);
}