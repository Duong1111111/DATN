package com.example.DATN.service.impls;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.DirectionRequestLog;
import com.example.DATN.entity.Location;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.DirectionRequestLogRepository;
import com.example.DATN.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectionRequestLogService {
    private final LocationRepository locationRepository;
    private final AccountRepository accountRepository;
    private final DirectionRequestLogRepository directionRequestLogRepository;

    public void logDirectionRequest(Integer locationId, String username) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));

        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        DirectionRequestLog log = new DirectionRequestLog();
        log.setLocation(location);
        log.setUser(user);

        directionRequestLogRepository.save(log);
    }
}
