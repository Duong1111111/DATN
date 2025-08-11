package com.example.DATN.controller;

import com.example.DATN.dto.request.NotificationToRoleRequest;
import com.example.DATN.dto.response.NotificationResponse;
import com.example.DATN.dto.response.SendNotificationSummaryResponse;
import com.example.DATN.repository.*;
import com.example.DATN.service.impls.NotificationService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;
    private final AccountRepository accountRepository;
    private final AdRepository adRepository;
    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final TimeAgoUtil timeAgoUtil;

    public NotificationController(NotificationService notificationService, CompanyRepository companyRepository, AccountRepository accountRepository, AdRepository adRepository, LocationRepository locationRepository, ReviewRepository reviewRepository, TimeAgoUtil timeAgoUtil) {
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.accountRepository = accountRepository;
        this.adRepository = adRepository;
        this.locationRepository = locationRepository;
        this.reviewRepository = reviewRepository;
        this.timeAgoUtil = timeAgoUtil;
    }

    @GetMapping("/company-pending")
    public ResponseEntity<BaseResponse<List<String>>> getAllCompanyPendingNotification(){
        List<String> message = notificationService.getAllCompanyPendingNotifications();
        return  ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, message));
    }
    @PostMapping("/send-to-role")
    public SendNotificationSummaryResponse sendNotificationToRole(
            @RequestParam Integer adminId,
            @RequestBody NotificationToRoleRequest request) {
        return notificationService.sendNotificationToRole(adminId, request);
    }
    @PutMapping("/{id}/read")
    public String markNotificationAsRead(@PathVariable Long id, @RequestParam Integer receiverId) {
        notificationService.markAsRead(id, receiverId);
        return "Notification marked as read";
    }
    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    // Lấy thông báo cho tất cả company mới đăng ký
    @GetMapping("/company-registered")
    public ResponseEntity<BaseResponse<List<String>>> getAllCompanyRegisteredNotifications() {
        List<String> messages = companyRepository.findAll().stream()
                .map(c -> timeAgoUtil.companyRegisteredAgo(c.getCompanyName(), c.getAccount().getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, messages));
    }

    // Lấy thông báo cho tất cả user mới tạo
    @GetMapping("/user-registered")
    public ResponseEntity<BaseResponse<List<String>>> getAllUserRegisteredNotifications() {
        List<String> messages = accountRepository.findAll().stream()
                .filter(a -> a.getRole() == Role.USER && a.getUser() != null)
                .map(a -> timeAgoUtil.userCreatedAgo(a.getUsername(), a.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, messages));
    }

    // Lấy thông báo cho tất cả quảng cáo
    @GetMapping("/ads-created")
    public ResponseEntity<BaseResponse<List<String>>> getAllAdCreatedNotifications() {
        List<String> messages = adRepository.findAll().stream()
                .map(ad -> "Quảng cáo \"" + ad.getLocation().getName() + "\" được tạo " + timeAgoUtil.timeAgo(ad.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, messages));
    }

    // Lấy thông báo cho tất cả địa điểm
    @GetMapping("/locations-created")
    public ResponseEntity<BaseResponse<List<String>>> getAllLocationCreatedNotifications() {
        List<String> messages = locationRepository.findAll().stream()
                .map(loc -> "Địa điểm \"" + loc.getName() + "\" được tạo " + timeAgoUtil.timeAgo(loc.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, messages));
    }

    // Lấy thông báo cho tất cả review
    @GetMapping("/reviews-created")
    public ResponseEntity<BaseResponse<List<String>>> getAllReviewCreatedNotifications() {
        List<String> messages = reviewRepository.findAll().stream()
                .map(r -> "Người dùng " + r.getUser().getUsername() +
                        " đã đánh giá \"" + r.getLocation().getName() + "\" " +
                        timeAgoUtil.timeAgo(r.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, messages));
    }

}
