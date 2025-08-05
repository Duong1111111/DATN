package com.example.DATN.utils.components;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeAgoUtil {

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
        return "Công ty " + companyName + " đã đăng ký " + timeAgo(createdAt);
    }

    public String userCreatedAgo(String username, LocalDateTime createdAt) {
        return "Tài khoản " + username + " được tạo " + timeAgo(createdAt);
    }
}
