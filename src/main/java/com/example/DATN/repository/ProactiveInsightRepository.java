package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.ProactiveInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProactiveInsightRepository extends JpaRepository<ProactiveInsight, Integer> {
    List<ProactiveInsight> findByCompanyAndIsReadFalseOrderByCreatedAtDesc(Account company);
}