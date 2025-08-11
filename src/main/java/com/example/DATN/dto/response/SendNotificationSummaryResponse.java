package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationSummaryResponse {
    private String message;
    private int totalReceivers;
    private Role targetRole;
    private List<String> receiverUsernames;

}
