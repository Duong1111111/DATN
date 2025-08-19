package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.options.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    private Long id;
    private Type type;
    private String content;
    private LocalDateTime createdAt;
    private Role targetRole;
    private String senderUsername;
    private String receiverUsername;
}
