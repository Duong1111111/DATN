package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationToRoleRequest {
    private Role targetRole;
    private String type;
    private String content;

}