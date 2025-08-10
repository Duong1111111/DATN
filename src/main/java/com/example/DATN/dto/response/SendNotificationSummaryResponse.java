package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendNotificationSummaryResponse {
    private String message;
    private int totalReceivers;
    private String targetRole;
    private List<String> receiverUsernames;

    public SendNotificationSummaryResponse(String message, int totalReceivers, String targetRole, List<String> receiverUsernames) {
        this.message = message;
        this.totalReceivers = totalReceivers;
        this.targetRole = targetRole;
        this.receiverUsernames = receiverUsernames;
    }

}
