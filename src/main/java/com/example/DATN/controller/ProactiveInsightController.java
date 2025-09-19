package com.example.DATN.controller;

import com.example.DATN.dto.response.ProactiveInsightResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.ProactiveInsight;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.impls.ProactiveInsightService;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proactive-insights")
@RequiredArgsConstructor
public class ProactiveInsightController {

    private final ProactiveInsightService insightService;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/unread")
    public ResponseEntity<List<ProactiveInsightResponse>> getUnreadInsights() {
        Account currentUser = getCurrentUser();
        List<ProactiveInsight> insights = insightService.getUnreadInsights(currentUser.getUserId());
        List<ProactiveInsightResponse> response = insights.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{insightId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer insightId) {
        Account currentUser = getCurrentUser();
        insightService.markAsRead(insightId, currentUser.getUserId());
        return ResponseEntity.ok().build();
    }

    private Account getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private ProactiveInsightResponse toResponse(ProactiveInsight insight) {
        ProactiveInsightResponse dto = new ProactiveInsightResponse();
        dto.setId(insight.getId());
        dto.setTitle(insight.getTitle());
        dto.setSummary(insight.getSummary());
        dto.setCreatedAt(insight.getCreatedAt());

        if (insight.getDetailsJson() != null && !insight.getDetailsJson().isEmpty()) {
            try {
                Map<String, Object> detailsMap = objectMapper.readValue(
                        insight.getDetailsJson(), new TypeReference<>() {});
                dto.setDetails(detailsMap);
            } catch (JsonProcessingException e) {
                dto.setDetails(null);
            }
        }
        return dto;
    }
}