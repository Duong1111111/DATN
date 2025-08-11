package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.Notification;
import com.example.DATN.utils.enums.options.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndReceiver_UserId(Long id, Integer receiverId);
    List<Notification> findByTargetRole(Role targetRole);
}
