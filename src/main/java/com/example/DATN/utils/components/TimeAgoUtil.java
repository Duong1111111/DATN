package com.example.DATN.utils.components;

import com.example.DATN.dto.response.NotificationResponse;
import com.example.DATN.entity.*;
import com.example.DATN.repository.*;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.options.Type;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeAgoUtil {

    private final NotificationRepository notificationRepository;

    public TimeAgoUtil(NotificationRepository notificationRepository, CompanyRepository companyRepository, AccountRepository accountRepository, AdRepository adRepository, LocationRepository locationRepository, ReviewRepository reviewRepository) {
        this.notificationRepository = notificationRepository;
    }

    public String timeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        if (duration.toDays() > 0) {
            return duration.toDays() + " ngày trước";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " giờ trước";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " phút trước";
        } else {
            return "vừa xong";
        }
    }
    private Notification notifyEntity(String content, Account sender, Role targetRole, Type type) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(targetRole);
        notification.setSender(sender);
        return notificationRepository.save(notification);
    }
    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setContent(notification.getContent());
        response.setCreatedAt(notification.getCreatedAt());
        response.setTargetRole(notification.getTargetRole());
        response.setSenderUsername(
                notification.getSender() != null ? notification.getSender().getUsername() : null
        );
        response.setReceiverUsername(
                notification.getReceiver() != null ? notification.getReceiver().getUsername() : null
        );
        return response;
    }

        public String companyRegisteredAgo(String companyName) {
            return "Công ty " + (companyName != null ? companyName : "Không rõ") + " đã đăng ký";
        }

        public String userCreatedAgo(String username) {
            return "Tài khoản " + (username != null ? username : "Không rõ") + " được tạo";
        }
        public NotificationResponse notifyCompanyRegistered(Company company) {
            Notification notification = new Notification();
            notification.setType(Type.INFO);
            notification.setContent(companyRegisteredAgo(company.getCompanyName()));
            notification.setCreatedAt(LocalDateTime.now());
            notification.setTargetRole(Role.STAFF);
            notification.setSender(company.getAccount());

            notificationRepository.save(notification);

            return toResponse(notification);
        }

        public NotificationResponse notifyUserRegistered(User user) {
            Notification notification = new Notification();
            notification.setType(Type.INFO);
            notification.setContent(userCreatedAgo(user.getAccount().getUsername()));
            notification.setCreatedAt(LocalDateTime.now());
            notification.setTargetRole(Role.STAFF);
            notification.setSender(user.getAccount());

            notificationRepository.save(notification);

            return toResponse(notification);
        }

        public NotificationResponse notifyAdCreated(Ad ad) {
            String content = "Quảng cáo " + ad.getLocation().getName()
                    + " được tạo " ;

            Notification notification = notifyEntity(content, ad.getCreatedBy(), Role.STAFF, Type.INFO);
            return toResponse(notification);
        }

        public NotificationResponse notifyLocationCreated(Location location) {
            String content = "Địa điểm " + location.getName()
                    + " được tạo " ;

            Notification notification = notifyEntity(content, location.getCreatedBy(), Role.STAFF, Type.INFO);
            return toResponse(notification);
        }

        public NotificationResponse notifyReviewCreated(Review review) {
            String content = "Người dùng " + review.getUser().getUsername()
                    + " đã đánh giá " + review.getLocation().getName();

            Notification notification = notifyEntity(content, review.getUser(), Role.STAFF, Type.INFO);
            return toResponse(notification);
        }

}
