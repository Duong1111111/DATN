package com.example.DATN.service.impls;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.Ad;
import com.example.DATN.entity.AdActionLog;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.AdActionLogRepository;
import com.example.DATN.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdActionLogService {
    private final AdRepository adRepository;
    private final AccountRepository accountRepository;
    private final AdActionLogRepository adActionLogRepository;

    public void logImpression(Ad ad, String username) {
        AdActionLog log = new AdActionLog();
        log.setAd(ad);
        log.setActionType("IMPRESSION");

        if (username != null) {
            Account user = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            log.setUser(user);
        }

        adActionLogRepository.save(log);
    }

    public void logClick(Integer adId, String username) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found with id: " + adId));

        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        AdActionLog log = new AdActionLog();
        log.setAd(ad);
        log.setUser(user);
        log.setActionType("CLICK");

        adActionLogRepository.save(log);
    }
}
