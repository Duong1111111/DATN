package com.example.DATN.service.impls;

import com.example.DATN.entity.Account;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final AccountRepository accountRepository;
    private final TimeAgoUtil timeAgoUtil;

    public NotificationService(AccountRepository accountRepository, TimeAgoUtil timeAgoUtil) {
        this.accountRepository = accountRepository;
        this.timeAgoUtil = timeAgoUtil;
    }

    public String getCompanyPendingNotification(Integer userId) {
        Account account = accountRepository.findByUserIdAndStatus(userId, AccountStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (account.getCompany() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND); // hoặc tạo mã lỗi riêng COMPANY_NOT_FOUND
        }

        return timeAgoUtil.companyRegisteredAgo(
                account.getCompany().getCompanyName(),
                account.getCreatedAt()
        );
    }
}
