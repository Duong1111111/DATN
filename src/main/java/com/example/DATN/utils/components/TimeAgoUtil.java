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
        Notification notification = new Notification();
        notification.setType(Type.INFO);
        notification.setContent("Quảng cáo \"" + ad.getLocation().getName()
                + "\" được tạo " + timeAgo(ad.getCreatedAt()));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(Role.STAFF);
        notification.setSender(ad.getCreatedBy());
        notificationRepository.save(notification);
    }

    public void notifyLocationCreated(Location location) {
        Notification notification = new Notification();
        notification.setType(Type.INFO);
        notification.setContent("Địa điểm \"" + location.getName()
                + "\" được tạo " + timeAgo(location.getCreatedAt()));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(Role.STAFF);
        notification.setSender(location.getCreatedBy());
        notificationRepository.save(notification);
    }

    public void notifyReviewCreated(Review review) {
        Notification notification = new Notification();
        notification.setType(Type.INFO);
        notification.setContent("Người dùng " + review.getUser().getUsername()
                + " đã đánh giá \"" + review.getLocation().getName()
                + "\" " + timeAgo(review.getCreatedAt()));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTargetRole(Role.STAFF);
        notification.setSender(review.getUser());
        notificationRepository.save(notification);
    }
}
