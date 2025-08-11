package com.example.DATN.service.impls;

import com.example.DATN.dto.request.NotificationToRoleRequest;
import com.example.DATN.dto.response.NotificationResponse;
import com.example.DATN.dto.response.SendNotificationSummaryResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Notification;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.NotificationRepository;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final AccountRepository accountRepository;
    private final TimeAgoUtil timeAgoUtil;
    private final NotificationRepository notificationRepository;

    public NotificationService(AccountRepository accountRepository, TimeAgoUtil timeAgoUtil, NotificationRepository notificationRepository) {
        this.accountRepository = accountRepository;
        this.timeAgoUtil = timeAgoUtil;
        this.notificationRepository = notificationRepository;
    }

    public String getCompanyPendingNotification(Integer userId) {
        Account account = accountRepository.findByUserIdAndStatus(userId, AccountStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (account.getCompany() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND); // hoặc tạo mã lỗi riêng COMPANY_NOT_FOUND
        }

        return timeAgoUtil.companyRegisteredAgo(
                account.getCompany().getCompanyName(),
                account.getCreatedAt()
        );
    }

    public List<String> getAllCompanyPendingNotifications() {
        return accountRepository.findByStatus(AccountStatus.PENDING).stream()
                .map(account -> timeAgoUtil.companyRegisteredAgo(
                        account.getCompany().getCompanyName(),
                        account.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public SendNotificationSummaryResponse sendNotificationToRole(Integer adminId, NotificationToRoleRequest request) {
        Account admin = accountRepository.findById(adminId)
                .filter(a -> a.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Only admin can send notifications"));

        List<Account> receivers = accountRepository.findByRole(request.getTargetRole());
        if (receivers.isEmpty()) {
            throw new RuntimeException("No users found with role: " + request.getTargetRole());
        }

        List<Notification> notifications = receivers.stream().map(receiver -> {
            Notification n = new Notification();
            n.setType(request.getType());
            n.setContent(request.getContent());
            n.setSender(admin);
            n.setReceiver(receiver);
            n.setTargetRole(request.getTargetRole());
            return n;
        }).toList();

        notificationRepository.saveAll(notifications);

        return new SendNotificationSummaryResponse(
                "Sent successfully",
                receivers.size(),
                request.getTargetRole(),
                receivers.stream().map(Account::getUsername).toList()
        );
    }

    public List<NotificationResponse> getNotificationsForUser(Integer userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByTargetRole(user.getRole()).stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getType(),
                        n.getContent(),
                        n.getCreatedAt(),
                        n.getTargetRole(),
                        n.getSender().getUsername(),
                        n.getReceiver() != null ? n.getReceiver().getUsername() : null
                ))
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, Integer receiverId) {
        Notification notification = notificationRepository.findByIdAndReceiver_UserId(notificationId, receiverId)
                .orElseThrow(() -> new RuntimeException("Notification not found or not belongs to user"));

        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

}
