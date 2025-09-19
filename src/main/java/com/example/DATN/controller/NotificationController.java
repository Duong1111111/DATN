package com.example.DATN.controller;

import com.example.DATN.dto.request.NotificationToRoleRequest;
import com.example.DATN.dto.response.NotificationResponse;
import com.example.DATN.dto.response.SendNotificationSummaryResponse;
import com.example.DATN.repository.*;
import com.example.DATN.service.impls.NotificationService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
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

    @GetMapping("/sent-by-admin")
    public List<NotificationResponse> getNotificationsSentByAdmin(
            @RequestParam Integer adminId) {
        return notificationService.getNotificationsSentByAdmin(adminId);
    }
    @GetMapping("/for-staff-from-admin")
    public List<NotificationResponse> getNotificationsForUserFromAdmin(
            @RequestParam Integer userId) {
        return notificationService.getNotificationsForUserFromAdmin(userId);
    }

    // Lấy thông báo cho tất cả company mới đăng ký
    @GetMapping("/company-registered")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAllCompanyRegisteredNotifications() {
        List<NotificationResponse> responses = companyRepository.findAll().stream()
                .map(c -> {
                    NotificationResponse res = new NotificationResponse();
                    res.setContent(timeAgoUtil.companyRegisteredAgo(c.getCompanyName()));
                    res.setCreatedAt(c.getAccount().getCreatedAt());
//                    res.setTargetRole(Role.STAFF);
//                    res.setSenderUsername(c.getAccount().getUsername());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, responses));
    }

    // Lấy thông báo cho tất cả user mới tạo
    @GetMapping("/user-registered")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAllUserRegisteredNotifications() {
        List<NotificationResponse> responses = accountRepository.findAll().stream()
                .filter(a -> a.getRole() == Role.USER && a.getUser() != null)
                .map(a -> {
                    NotificationResponse res = new NotificationResponse();
                    res.setContent(timeAgoUtil.userCreatedAgo(a.getUsername()));
                    res.setCreatedAt(a.getCreatedAt());
//                    res.setTargetRole(Role.STAFF);
//                    res.setSenderUsername(a.getUsername());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, responses));
    }

    // Lấy thông báo cho tất cả quảng cáo
    @GetMapping("/ads-created")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAllAdCreatedNotifications() {
        List<NotificationResponse> responses = adRepository.findAll().stream()
                .map(ad -> {
                    NotificationResponse res = new NotificationResponse();
                    res.setContent("Quảng cáo " + ad.getLocation().getName() + " đã được tạo ");
                    res.setCreatedAt(ad.getCreatedAt());
//                    res.setTargetRole(Role.STAFF);
//                    res.setSenderUsername(ad.getCreatedBy().getUsername());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, responses));
    }

    // Lấy thông báo cho tất cả địa điểm
    @GetMapping("/locations-created")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAllLocationCreatedNotifications() {
        List<NotificationResponse> responses = locationRepository.findAll().stream()
                .map(loc -> {
                    NotificationResponse res = new NotificationResponse();
                    res.setContent("Địa điểm " + loc.getName() + " đã được tạo ");
                    res.setCreatedAt(loc.getCreatedAt());
//                    res.setTargetRole(Role.STAFF);
//                    res.setSenderUsername(loc.getCreatedBy().getUsername());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, responses));
    }

    // Lấy thông báo cho tất cả review
    @GetMapping("/reviews-created")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAllReviewCreatedNotifications() {
        List<NotificationResponse> responses = reviewRepository.findAll().stream()
                .map(r -> {
                    NotificationResponse res = new NotificationResponse();
                    res.setContent("Người dùng " + r.getUser().getUsername() +
                            " đã đánh giá " + r.getLocation().getName() + " ");
                    res.setCreatedAt(r.getCreatedAt());
//                    res.setTargetRole(Role.STAFF);
//                    res.setSenderUsername(r.getUser().getUsername());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, responses));
    }

}
