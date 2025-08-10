package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.Type;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {
    private Long id;
    private Type type;
    private String content;
    private LocalDateTime createdAt;
    private String senderUsername;
    private String receiverUsername;
}
