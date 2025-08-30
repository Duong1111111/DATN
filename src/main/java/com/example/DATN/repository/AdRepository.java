package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.Ad;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    List<Ad> findAllByStatus(AccountStatus status);
    List<Ad> findByCreatedBy(Account account);
}