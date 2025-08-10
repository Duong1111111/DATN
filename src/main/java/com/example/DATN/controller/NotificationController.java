package com.example.DATN.controller;

import com.example.DATN.dto.request.NotificationToRoleRequest;
import com.example.DATN.dto.response.SendNotificationSummaryResponse;
import com.example.DATN.service.impls.NotificationService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/company-pending/{userId}")
    public ResponseEntity<BaseResponse<String>> getCompanyPendingNotification(@PathVariable Integer userId) {
        String message = notificationService.getCompanyPendingNotification(userId);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, message));
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

}
