package com.example.DATN.utils.components;

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
    private void notifyEntity(String content, Account sender, Role targetRole, Type type) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(targetRole);
        notification.setSender(sender);
        notificationRepository.save(notification);
    }

    public String companyRegisteredAgo(String companyName, LocalDateTime createdAt) {
        String name = (companyName != null) ? companyName : "Không rõ";
        LocalDateTime date = (createdAt != null) ? createdAt : LocalDateTime.now();
        return "Công ty " + name + " đã đăng ký " + timeAgo(date);
    }

    public String userCreatedAgo(String username, LocalDateTime createdAt) {
        String name = (username != null) ? username : "Không rõ";
        LocalDateTime date = (createdAt != null) ? createdAt : LocalDateTime.now();
        return "Tài khoản " + name + " được tạo " + timeAgo(date);
    }
    public void notifyCompanyRegistered(Company company) {
        Notification notification = new Notification();
        notification.setType(Type.INFO);
        notification.setContent(
                companyRegisteredAgo(company.getCompanyName(), company.getAccount().getCreatedAt())
        );
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(Role.STAFF);
        notification.setSender(company.getAccount());
        notificationRepository.save(notification);
    }

    public void notifyUserRegistered(User user) {
        Notification notification = new Notification();
        notification.setType(Type.INFO);
        notification.setContent(
                userCreatedAgo(user.getAccount().getUsername(), user.getAccount().getCreatedAt())
        );
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(Role.STAFF);
        notification.setSender(user.getAccount());
        notificationRepository.save(notification);
    }
    public void notifyAdCreated(Ad ad) {
        String content = "Quảng cáo \"" + ad.getLocation().getName()
                + "\" được tạo " + timeAgo(ad.getCreatedAt());
        notifyEntity(content, ad.getCreatedBy(), Role.STAFF, Type.INFO);
    }

    public void notifyLocationCreated(Location location) {
        String content = "Địa điểm \"" + location.getName()
                + "\" được tạo " + timeAgo(location.getCreatedAt());
        notifyEntity(content, location.getCreatedBy(), Role.STAFF, Type.INFO);
    }

    public void notifyReviewCreated(Review review) {
        String content = "Người dùng " + review.getUser().getUsername()
                + " đã đánh giá \"" + review.getLocation().getName()
                + "\" " + timeAgo(review.getCreatedAt());
        notifyEntity(content, review.getUser(), Role.STAFF, Type.INFO);
    }

}
