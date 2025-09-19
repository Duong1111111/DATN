package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.options.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationToRoleRequest {
    private Role targetRole;
    private Type type;
    private String title;
    private String content;

    public void setTargetRole(String targetRole) {
        if (targetRole != null) {
            this.targetRole = Role.valueOf(targetRole.toUpperCase());
        }
    }
    public void setType(String type){
        if (type != null){
            this.type = Type.valueOf(type.toUpperCase());
        }
    }

}